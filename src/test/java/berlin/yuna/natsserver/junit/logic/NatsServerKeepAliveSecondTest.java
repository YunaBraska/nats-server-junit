package berlin.yuna.natsserver.junit.logic;

import berlin.yuna.natsserver.junit.model.annotation.JUnitNatsServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static berlin.yuna.natsserver.junit.logic.NatsServer.getNatsServerByName;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@Tag("IntegrationTest")
@ExtendWith(NatsServer.class)
@JUnitNatsServer(port = -1, name = "RandomNats", keepAlive = true)
class NatsServerKeepAliveSecondTest {

    @Test
    void natsServerShouldStart() {
        final NatsServer natsServer = getNatsServerByName("RandomNats");
        assertThat(natsServer, is(notNullValue()));
        assertThat(natsServer.getPort(), is(not(-1)));
    }
}
