package berlin.yuna.natsserver.junit.logic;

import berlin.yuna.natsserver.junit.model.annotation.JUnitNatsServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static berlin.yuna.natsserver.config.NatsConfig.ADDR;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_BINARY_PATH;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_CONFIG_FILE;
import static berlin.yuna.natsserver.junit.logic.NatsServer.NATS_SERVER_LIST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@Tag("IntegrationTest")
@JUnitNatsServer(port = 4680, binaryFile = " ", configFile = "invalid.file.path")
class NatsServerCoverageTest {

    final NatsServer natsServer = NATS_SERVER_LIST.iterator().next();

    @Test
    void coverageTest() {
        assertThat(natsServer.getConfig(NATS_CONFIG_FILE), is("invalid.file.path"));
        assertThat(natsServer.getConfig(ADDR), is(ADDR.value()));
        assertThat(natsServer.getConfig(NATS_BINARY_PATH), is(nullValue()));
        assertThat(natsServer.getConfig(NATS_BINARY_PATH, () -> "fallback"), is(equalTo("fallback")));
        assertThat(natsServer.getTimeoutMs(), is(equalTo(10000L)));
        assertThat(natsServer.hashCode(), is(not(-1)));
        assertThat(natsServer, is(equalTo(natsServer)));
        assertThat(natsServer, is(not(equalTo(new NatsServer()))));
        assertThat(natsServer.toString(), is(notNullValue()));
    }
}