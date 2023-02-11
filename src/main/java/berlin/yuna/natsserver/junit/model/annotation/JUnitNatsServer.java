package berlin.yuna.natsserver.junit.model.annotation;


import berlin.yuna.natsserver.config.NatsConfig;
import berlin.yuna.natsserver.junit.logic.NatsServer;
import berlin.yuna.natsserver.logic.Nats;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(NatsServer.class)
public @interface JUnitNatsServer {
    /**
     * Sets nats port
     * -1 means random port
     */
    int port() default 4222;

    /**
     * Defines the startup and teardown timeout
     */
    long timeoutMs() default 10000;

    /**
     * Nats server name
     */
    String name() default "";

    /**
     * Config file
     */
    String configFile() default "";

    /**
     * Custom download URL
     */
    String downloadUrl() default "";

    /**
     * File to nats server binary so no download will be needed
     */
    String binaryFile() default "";

    /**
     * Passes the original parameters to {@link Nats#config()} for startup
     * {@link berlin.yuna.natsserver.config.NatsConfig}
     */
    String[] config() default {};

    /**
     * Prevents the {@link NatsServer} from recreating for each test class
     */
    boolean keepAlive() default false;
}
