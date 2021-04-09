package org.springframework.fu.jafu.redis;

import org.springframework.boot.autoconfigure.data.redis.JedisRedisInitializer;
import org.springframework.boot.autoconfigure.data.redis.RedisInitializer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.FeatureFunction;

import java.util.function.Consumer;

/**
 * JaFu DSL for Redis configuration.
 *
 * @author Andreas Gebhardt
 */
public class RedisDsl extends AbstractRedisDsl<RedisDsl> {

    private RedisDsl(GenericApplicationContext context) {
        super(context);
    }

    public RedisDsl jedis() {
        return jedis(dsl -> {
        });
    }

    @Override
    protected RedisDsl getSelf() {
        return this;
    }

    public RedisDsl jedis(final Consumer<JedisDsl> jedis) {
        setRedisClientInitializer(new JedisRedisInitializer(properties));
        jedis.accept(new JedisDsl(properties.getJedis()));
        return this;
    }

    public static FeatureFunction<RedisDsl> redis() {
        return FeatureFunction.of(RedisDsl::new, RedisDsl::afterConfiguration);
    }

    private void afterConfiguration() {
        new RedisInitializer().initialize(applicationContext);
    }

    public static class JedisDsl {

        private final RedisProperties.Jedis jedis;

        JedisDsl(final RedisProperties.Jedis jedis) {
            this.jedis = jedis;
            this.jedis.setPool(new RedisProperties.Pool());
        }

        public JedisDsl pool(final Consumer<PoolDsl> pool) {
            pool.accept(new PoolDsl(this.jedis.getPool()));
            return this;
        }

    }
}
