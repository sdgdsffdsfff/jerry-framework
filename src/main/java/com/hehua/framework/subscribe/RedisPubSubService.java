/**
 * 
 */
package com.hehua.framework.subscribe;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ShardedJedis;

import com.google.common.collect.ArrayListMultimap;
import com.hehua.framework.jedis.PoolableJedis;
import com.hehua.framework.jedis.PoolableJedisFactory;
import com.hehua.framework.jedis.PoolableJedisManager;

/**
 * TODO
 * 
 * @author zhihua
 *
 */
public class RedisPubSubService implements PubSubService {

    private static final Logger logger = LoggerFactory.getLogger(RedisPubSubService.class);

    private static final RedisPubSubService instance = new RedisPubSubService();

    private RedisPubSubService() {
        init();
    }

    public static RedisPubSubService getInstance() {
        return instance;
    }

    private static final String KEY_PREFIX = "pubsub:";

    private ShardedJedis subscriberJedis;

    private PoolableJedis publisherJedis;

    private int capacity = 2000;

    private final ArrayListMultimap<String, Subscriber> subscribers = ArrayListMultimap.create();

    public synchronized void subscribe(String channel, Subscriber subscriber) {
        if (subscribers.size() >= capacity) {
            throw new RuntimeException("add subscribe fail, too much subscribers");
        }
        subscribers.put(channel, subscriber);
    }

    public synchronized void unsubscribe(String channel, Subscriber subscriber) {
        subscribers.remove(channel, subscriber);
    }

    public synchronized void init() {

        ShardedJedis oldJedis = this.subscriberJedis;
        if (oldJedis != null) {
            oldJedis.close();
        }

        ShardedJedis newJedis = PoolableJedisFactory.getInstance().createShardJedis("cache1");

        Collection<Jedis> allShards = newJedis.getAllShards();
        ExecutorService executor = Executors.newFixedThreadPool(allShards.size());
        for (final Jedis shard : allShards) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            shard.connect();
                            shard.psubscribe(new RedisMessageBus(), KEY_PREFIX + "*");
                        } catch (Exception e) {
                            logger.error("psubscribe error", e);
                        }

                        try {
                            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                        } catch (InterruptedException e) {
                            logger.error("sleep interrputed", e);
                        }
                    }
                }
            });
        }
        this.subscriberJedis = newJedis;
        this.publisherJedis = PoolableJedisManager.getDefaultCacheJedis();
    }

    public synchronized void healthcheck() {

        if (logger.isDebugEnabled()) {
            logger.debug("healthcheck start");
        }
        System.out.println("healthcheck start");
        Collection<Jedis> allShards = subscriberJedis.getAllShards();
        for (Jedis shard : allShards) {
            try {
                if (shard.isConnected()) {
                    continue;
                }

                shard.connect();
                shard.psubscribe(new RedisMessageBus(), KEY_PREFIX + "*");
            } catch (Exception e) {
                logger.error("connect error.", e);
            }
        }
    }

    @Override
    public void post(String key, String message) {
        String fullkey = KEY_PREFIX + key;
        publisherJedis.publish(fullkey, message);
    }

    @Override
    public void register(String key, Subscriber subscriber) {
        subscribe(KEY_PREFIX + key, subscriber);
    }

    public void unregister(String key, Subscriber subscriber) {
        unsubscribe(KEY_PREFIX + key, subscriber);
    }

    private synchronized void dispatch(String channel, String message) {
        System.out.println("dispatch channel=" + channel + ", message=" + message);
        List<Subscriber> listeners = subscribers.get(channel);
        for (Subscriber subscriber : listeners) {
            try {
                subscriber.onMessage(message);
            } catch (Exception e) {
                logger.error("subscriber error", e);
            }
        }
    }

    class RedisMessageBus extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {
            dispatch(channel, message);
        }

        @Override
        public void onPMessage(String pattern, String channel, String message) {
            dispatch(channel, message);
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {

        }

        @Override
        public void onUnsubscribe(String channel, int subscribedChannels) {

        }

        @Override
        public void onPUnsubscribe(String pattern, int subscribedChannels) {

        }

        @Override
        public void onPSubscribe(String pattern, int subscribedChannels) {

        }

    }

    public static void main(String[] args) {

        RedisPubSubService.getInstance().register("test", new Subscriber() {

            @Override
            public void onMessage(Object message) {
                System.out.println("message: " + message);
            }

            @Override
            public String key() {
                return "test";
            }
        });

        for (int i = 1; i <= 1000; i++) {
            System.out.println("===post" + i);
            RedisPubSubService.getInstance().post("test", "hello pubsub");
        }
    }

}
