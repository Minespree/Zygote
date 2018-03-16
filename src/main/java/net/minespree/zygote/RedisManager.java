package net.minespree.zygote;

import lombok.Getter;
import net.minespree.zygote.config.RedisConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @since 02/02/2018
 */
@Getter
public class RedisManager {
    private final JedisPool pool;

    public RedisManager(RedisConfig config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getMaxConnections());

        pool = new JedisPool(poolConfig, config.getHost(), config.getPort(), 0, config.getPassword());
    }

    public Jedis getResource() {
        return pool.getResource();
    }

    public void close() {
        pool.close();
    }

    public boolean isClosed() {
        return pool != null && pool.isClosed();
    }
}
