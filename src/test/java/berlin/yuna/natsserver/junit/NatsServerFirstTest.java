package berlin.yuna.natsserver.junit;

import berlin.yuna.natsserver.junit.logic.NatsServer;
import berlin.yuna.natsserver.junit.model.annotation.JUnitNatsServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static berlin.yuna.natsserver.junit.logic.NatsServer.NATS_SERVER_LIST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@Tag("IntegrationTest")
@JUnitNatsServer(port = 4680)
class NatsServerFirstTest {

    private NatsServer previousNats;

    @ParameterizedTest
    @ArgumentsSource(NatsServer.class)
    void natsServerShouldStart(final NatsServer natsServer) {
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.getPort(), is(4680L));
        assertThat(natsServer.getPid(), is(not(-1)));
        assertThat(natsServer.getHost(), is(not(nullValue())));

        setPreviousNatsIfNotExists(natsServer);
        assertThat(previousNats, is(equalTo(natsServer)));
        assertThat(previousNats.getPid(), is(equalTo(natsServer.getPid())));

        assertThat(NATS_SERVER_LIST, is(hasItems(natsServer)));
        assertThat(NATS_SERVER_LIST.size(), is(1));
    }

    @ParameterizedTest
    @ArgumentsSource(NatsServer.class)
    void natsServerBeAlreadyStarted(final NatsServer natsServer) {
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.getPort(), is(4680L));
        assertThat(natsServer.getPid(), is(not(-1)));
        assertThat(natsServer.getHost(), is(not(nullValue())));

        setPreviousNatsIfNotExists(natsServer);
        assertThat(previousNats, is(equalTo(natsServer)));
        assertThat(previousNats.getPid(), is(equalTo(natsServer.getPid())));

        assertThat(NATS_SERVER_LIST, is(hasItems(natsServer)));
        assertThat(NATS_SERVER_LIST.size(), is(1));
    }

    private void setPreviousNatsIfNotExists(final NatsServer natsServer) {
        previousNats = previousNats == null ? natsServer : previousNats;
    }
}
