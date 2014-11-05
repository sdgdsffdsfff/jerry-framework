/**
 * 
 */
package com.hehua.framework.cache;

import com.google.common.base.Function;
import com.hehua.framework.codec.Codec;
import com.hehua.framework.jedis.PoolableJedis;

/**
 * @author zhihua
 *
 */
public class RedisCache<K, V> extends AbstractRedisCache<K, V> {

    private final Function<K, String> keyBuilder;

    private final Codec<V, String> valueCodec;

    /**
     * @param jedis
     */
    public RedisCache(PoolableJedis jedis, Function<K, String> keyBuilder, Codec<V, String> valueCodec) {
        super(jedis);
        this.keyBuilder = keyBuilder;
        this.valueCodec = valueCodec;
    }

    @Override
    public String encode(V object) {
        return valueCodec.encode(object);
    }

    @Override
    public V decode(String object) {
        return valueCodec.decode(object);
    }

    @Override
    public String buildKey(K key) {
        return keyBuilder.apply(key);
    }

}
