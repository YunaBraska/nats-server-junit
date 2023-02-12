package berlin.yuna.natsserver.junit.logic;

import berlin.yuna.natsserver.junit.model.annotation.JUnitNatsServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static berlin.yuna.natsserver.config.NatsConfig.NET;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_BINARY_PATH;
import static berlin.yuna.natsserver.config.NatsConfig.NATS_PROPERTY_FILE;
import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServer;
import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServerBy;
import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServerByHost;
import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServerByName;
import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServerByPid;
import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServerByPort;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@Tag("IntegrationTest")
@JUnitNatsServer(port = 4680, binaryFile = " ", configFile = "invalid.file.path")
class NatsServerCoverageTest {

    final NatsServer natsServer = getNatsServerByPort(4680);

    @Test
    void coverageTest() {
        assertThat(natsServer.getConfig(NATS_PROPERTY_FILE), is("invalid.file.path"));
        assertThat(natsServer.getConfig(NET), is(NET.defaultValue()));
        assertThat(natsServer.getConfig(NATS_BINARY_PATH), is(nullValue()));
        assertThat(natsServer.getConfig(NATS_BINARY_PATH, () -> "fallback"), is(equalTo("fallback")));
        assertThat(natsServer.getTimeoutMs(), is(equalTo(10000L)));
        assertThat(natsServer.hashCode(), is(not(-1)));
        assertThat(natsServer, is(equalTo(natsServer)));
        assertThat(natsServer, is(not(equalTo(new NatsServer()))));
        assertThat(natsServer.toString(), is(notNullValue()));
        assertThat(getNatsServer(), is(notNullValue()));
        assertThat(getNatsServerByHost(natsServer.getHost()), is(notNullValue()));
        assertThat(getNatsServerByPid(natsServer.getPid()), is(notNullValue()));
        assertThat(getNatsServerByPort(natsServer.getPort()), is(notNullValue()));
        assertThat(getNatsServerByName(natsServer.getName()), is(notNullValue()));
        assertThat(getNatsServerBy(Objects::nonNull), is(notNullValue()));
    }
}
