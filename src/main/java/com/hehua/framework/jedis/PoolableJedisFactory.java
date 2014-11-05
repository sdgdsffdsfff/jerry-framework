/**
 * 
 */
package com.hehua.framework.jedis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author zhihua
 *
 */
public class PoolableJedisFactory {

    private static final PoolableJedisFactory instance = new PoolableJedisFactory();

    private PoolableJedisFactory() {
    }

    public static PoolableJedisFactory getInstance() {
        return instance;
    }

    private static final int POOL_TOTAL_MAX_COUNT = 500;

    private static final int POOL_MAX_COUNT = 10;

    private static final Log logger = LogFactory.getLog(PoolableJedisFactory.class);

    private JedisConfigManager configManager = new JedisConfigManager();

    public PoolableJedis create(String name) {
        JedisClusterConfig config = configManager.getConfig(name);
        return create(config);
    }

    public PoolableJedis create(JedisClusterConfig config) {
        ShardedJedisPool jedisPool = initPool(config);
        return new PoolableJedis(jedisPool);
    }

    private ShardedJedisPool initPool(JedisClusterConfig config) {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMaxTotal(POOL_TOTAL_MAX_COUNT);
            poolConfig.setMaxIdle(POOL_MAX_COUNT);
            poolConfig.setBlockWhenExhausted(true);
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(config.getNodes().size());
            for (JedisNodeConfig nodeConfig : config.getNodes()) {
                JedisShardInfo jsi = new JedisShardInfo(nodeConfig.getHost(), nodeConfig.getPort(),
                        0, nodeConfig.getHost() + ":" + nodeConfig.getPort());
                shards.add(jsi);
            }
            return new ShardedJedisPool(poolConfig, shards);
        } catch (Throwable e) {
            logger.error("Ops.", e);
            throw new RuntimeException(e);
        }
    }

    public ShardedJedis createShardJedis(String name) {
        JedisClusterConfig config = configManager.getConfig(name);
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(config.getNodes().size());
        for (JedisNodeConfig nodeConfig : config.getNodes()) {
            JedisShardInfo jsi = new JedisShardInfo(nodeConfig.getHost(), nodeConfig.getPort(), 0,
                    nodeConfig.getHost() + ":" + nodeConfig.getPort());
            shards.add(jsi);
        }
        return new ShardedJedis(shards);
    }
}
