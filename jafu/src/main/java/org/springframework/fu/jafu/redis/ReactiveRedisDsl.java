package org.springframework.fu.jafu.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisReactiveInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.FeatureFunction;

import java.util.function.Consumer;

/**
 * JaFu DSL for reactive Redis configuration.
 *
 * @author Andreas Gebhardt
 */
public class ReactiveRedisDsl extends AbstractRedisDsl<ReactiveRedisDsl> {

    private ReactiveRedisDsl(GenericApplicationContext context) {
        super(context);
    }

    @Override
    protected ReactiveRedisDsl getSelf() {
        return this;
    }

    public static FeatureFunction<ReactiveRedisDsl> reactiveRedis() {
        return FeatureFunction.of(ReactiveRedisDsl::new, ReactiveRedisDsl::afterConfiguration);
    }

    private void afterConfiguration() {
        new RedisReactiveInitializer().initialize(applicationContext);
    }

}
