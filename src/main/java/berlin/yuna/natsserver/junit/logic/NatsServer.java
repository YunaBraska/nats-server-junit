package berlin.yuna.natsserver.junit.logic;

import berlin.yuna.natsserver.config.NatsConfig;
import berlin.yuna.natsserver.junit.model.annotation.JUnitNatsServer;
import berlin.yuna.natsserver.logic.Nats;
import berlin.yuna.natsserver.model.exception.NatsStartException;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static berlin.yuna.natsserver.config.NatsConfig.ADDR;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_BINARY_PATH;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_CONFIG_FILE;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_DOWNLOAD_URL;
import static berlin.yuna.natsserver.config.NatsConfig.PORT;

@SuppressWarnings("java:S2386")
public class NatsServer implements BeforeAllCallback, AfterAllCallback, ArgumentsProvider {

    private Nats nats;
    private long timeoutMs;
    private int pid;
    private long port;
    private String host;
    public static final List<NatsServer> NATS_SERVER_LIST = new CopyOnWriteArrayList<>();

    @Override
    public void beforeAll(final ExtensionContext context) {
        context.getElement().map(annotation -> annotation.getAnnotation(JUnitNatsServer.class)).ifPresent(config -> {
            nats = new Nats();
            timeoutMs = config.timeoutMs();
            if (config.port() != nats.port()) {
                nats.config(PORT, String.valueOf(config.port()));
            }
            nats.config(config.config());
            configure(nats, NATS_CONFIG_FILE, config.configFile());
            configure(nats, NATS_BINARY_PATH, config.binaryFile());
            configure(nats, NATS_DOWNLOAD_URL, config.downloadUrl());

            try {
                nats.start(timeoutMs);
                port = nats.port();
                pid = nats.pid();
                host = nats.getValue(ADDR);
                NATS_SERVER_LIST.add(this);
            } catch (Exception e) {
                nats.stop(timeoutMs);
                NATS_SERVER_LIST.remove(this);
                throw new NatsStartException(e);
            }
        });
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        if (nats != null) {
            nats.stop(timeoutMs);
            NATS_SERVER_LIST.remove(this);
        }
    }

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) {
        return NATS_SERVER_LIST.stream().map(Arguments::of);
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

    public long getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    private void configure(final Nats nats, final NatsConfig key, final String value) {
        if (hasText(value)) {
            nats.config(key, value);
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