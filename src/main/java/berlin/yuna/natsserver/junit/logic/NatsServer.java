package berlin.yuna.natsserver.junit.logic;

import berlin.yuna.natsserver.config.NatsConfig;
import berlin.yuna.natsserver.config.NatsOptions;
import berlin.yuna.natsserver.config.NatsOptionsBuilder;
import berlin.yuna.natsserver.junit.model.annotation.JUnitNatsServer;
import berlin.yuna.natsserver.logic.Nats;
import berlin.yuna.natsserver.model.exception.NatsStartException;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static berlin.yuna.natsserver.config.NatsConfig.ADDR;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_BINARY_PATH;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_DOWNLOAD_URL;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_LOG_NAME;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_PROPERTY_FILE;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_TIMEOUT_MS;
import static berlin.yuna.natsserver.config.NatsConfig.PORT;
import static berlin.yuna.natsserver.config.NatsOptions.natsBuilder;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@SuppressWarnings({"java:S2386", "unused", "UnusedReturnValue"})
public class NatsServer implements BeforeAllCallback, AfterAllCallback, ArgumentsProvider, ExtensionContext.Store.CloseableResource {

    private int pid;
    private Nats nats;
    private int port;
    private String name;
    private String host;
    private long timeoutMs;
    private boolean keepAlive;
    private static final List<NatsServer> NATS_SERVER_LIST = new CopyOnWriteArrayList<>();

    /**
     * Returns last running {@link NatsServer}
     *
     * @return {@link NatsServer} or null if no server is running
     */
    public static NatsServer getNatsServer() {
        return NATS_SERVER_LIST.isEmpty() ? null : NATS_SERVER_LIST.get(NATS_SERVER_LIST.size() - 1);
    }

    /**
     * Returns first running {@link NatsServer} with name
     *
     * @return {@link NatsServer} or null on no match
     */
    public static NatsServer getNatsServerByName(final String name) {
        return getNatsServerBy(natsServer -> natsServer.getName().equals(name));
    }

    /**
     * Returns first running {@link NatsServer} with pid (processId)
     *
     * @return {@link NatsServer} or null on no match
     */
    public static NatsServer getNatsServerByPid(final int pid) {
        return getNatsServerBy(natsServer -> natsServer.getPid() == pid);
    }

    /**
     * Returns first running {@link NatsServer} with port (For random port please identify by name)
     *
     * @return {@link NatsServer} or null on no match
     */
    public static NatsServer getNatsServerByPort(final int port) {
        return getNatsServerBy(natsServer -> natsServer.getPort() == port);
    }

    /**
     * Returns first running {@link NatsServer} with host
     *
     * @return {@link NatsServer} or null on no match
     */
    public static NatsServer getNatsServerByHost(final String host) {
        return getNatsServerBy(natsServer -> natsServer.getHost().equals(host));
    }

    /**
     * Returns first running {@link NatsServer} with filter
     *
     * @return {@link NatsServer} or null on no match
     */
    public static NatsServer getNatsServerBy(final Predicate<NatsServer> filter) {
        return NATS_SERVER_LIST.stream().filter(filter).findFirst().orElse(null);
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        context.getElement().map(annotation -> annotation.getAnnotation(JUnitNatsServer.class)).ifPresent(config -> {
            validateKeepAlive(context, config);
            final NatsOptionsBuilder options = natsBuilder();
            if (config.port() != (Integer) PORT.defaultValue()) {
                options.config(PORT, String.valueOf(config.port()));
            }
            options.config(config.config()).timeoutMs(config.timeoutMs());
            configure(options, NATS_PROPERTY_FILE, config.configFile());
            configure(options, NATS_BINARY_PATH, config.binaryFile());
            configure(options, NATS_DOWNLOAD_URL, config.downloadUrl());
            configure(options, NATS_LOG_NAME, config.name());
            configure(options, NATS_TIMEOUT_MS, String.valueOf(config.timeoutMs()));

            try {
                start(options.build(), context, config);
                this.port = nats.port();
                this.pid = nats.pid();
                this.host = nats.getValue(ADDR);
                this.name = nats.getValue(NATS_LOG_NAME);
                this.timeoutMs = config.timeoutMs();
                this.keepAlive = config.keepAlive();
                NATS_SERVER_LIST.add(this);
            } catch (Exception e) {
                ofNullable(nats).ifPresent(Nats::close);
                NATS_SERVER_LIST.remove(this);
                throw new NatsStartException(e);
            }
        });
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        if (nats != null && !keepAlive) {
            nats.close();
            NATS_SERVER_LIST.remove(this);
        }
    }

    @Override
    public void close() {
        NATS_SERVER_LIST.forEach(NatsServer::stop);
    }

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
        return NATS_SERVER_LIST.isEmpty() ? Stream.empty() : Stream.of(Arguments.of(NATS_SERVER_LIST.get(NATS_SERVER_LIST.size() - 1)));
    }

    public String getConfig(final NatsConfig key) {
        return nats.getValue(key);
    }

    public String getConfig(final NatsConfig key, final Supplier<String> or) {
        return nats.getValue(key, or);
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public int getPid() {
        return pid;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public Nats stop() {
        nats.close();
        return nats;
    }

    public Nats getNats() {
        return nats;
    }

    private void configure(final NatsOptionsBuilder options, final NatsConfig key, final String value) {
        if (hasText(value)) {
            options.config(key, value);
        }
    }

    private static boolean hasText(final String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    private static boolean containsText(final CharSequence str) {
        final int strLen = str.length();

        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private void start(final NatsOptions options, final ExtensionContext context, final JUnitNatsServer config) {
        final String stayAliveName = ofNullable(options.config().get(NATS_LOG_NAME)).filter(NatsServer::hasText).orElse(NATS_LOG_NAME.defaultValueStr());
        final NatsServer prevNatsServer = config.keepAlive() ? getNatsServerByName(stayAliveName) : null;
        if (prevNatsServer != null) {
            this.nats = prevNatsServer.getNats();
        } else {
            this.nats = new Nats(options);
            if (config.keepAlive()) {
                context.getRoot().getStore(GLOBAL).put(stayAliveName, this);
            }
        }
    }

    private void validateKeepAlive(final ExtensionContext context, final JUnitNatsServer config) {
        final Optional<Optional<Class<? extends Extension>>> closeableResource = context.getElement().map(annotation -> annotation.getAnnotation(ExtendWith.class)).map(extendWith -> Arrays.stream(extendWith.value()).filter(aClass -> aClass == NatsServer.class).findFirst());
        if (config.keepAlive() && closeableResource.isEmpty()) {
            throw new IllegalStateException("Missing annotation [@ExtendWith(" + NatsServer.class.getSimpleName() + ".class)] in addition of the [keepAlive] flag");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NatsServer that = (NatsServer) o;
        return pid == that.pid && port == that.port && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, port, host);
    }

    @Override
    public String toString() {
        return "NatsServer{" +
                "timeoutMs=" + timeoutMs +
                ", pid=" + pid +
                ", port=" + port +
                ", host='" + host + '\'' +
                '}';
    }
}
