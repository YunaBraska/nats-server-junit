package berlin.yuna.natsserver.junit.logic;

import berlin.yuna.natsserver.junit.model.annotation.JUnitNatsServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServerByPort;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@Tag("IntegrationTest")
@JUnitNatsServer(port = 4680)
class NatsServerSecondTest {

    private NatsServer previousNats;

    @Test
    void natsServerShouldStart() {
        final NatsServer natsServer = getNatsServerByPort(4680);
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.getPort(), is(4680));
        assertThat(natsServer.getPid(), is(not(-1)));
        assertThat(natsServer.getHost(), is(not(nullValue())));

        setPreviousNatsIfNotExists(natsServer);
        assertThat(previousNats, is(equalTo(natsServer)));
        assertThat(previousNats.getPid(), is(equalTo(natsServer.getPid())));
    }

    @Test
    void natsServerBeAlreadyStarted() {
        final NatsServer natsServer = getNatsServerByPort(4680);
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.getPort(), is(4680));
        assertThat(natsServer.getPid(), is(not(-1)));
        assertThat(natsServer.getHost(), is(not(nullValue())));

        setPreviousNatsIfNotExists(natsServer);
        assertThat(previousNats, is(equalTo(natsServer)));
        assertThat(previousNats.getPid(), is(equalTo(natsServer.getPid())));
    }

    private void setPreviousNatsIfNotExists(final NatsServer natsServer) {
        previousNats = previousNats == null ? natsServer : previousNats;
    }
}
