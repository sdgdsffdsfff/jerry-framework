/**
 * 
 */
package com.hehua.framework.subscribe;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.hehua.framework.zookeeper.ZookeeperClientFactory;

public final class ZookeeperPubSubService implements Watcher, PubSubService {

    private static final int INIT_FAIL_RETRY_TIMES = 10;

    private static final long INIT_FAIL_RETRY_DELAY = 200;

    private static final int RELOAD_FAIL_RETRY_TIMES = 3;

    private static final long RELOAD_FAIL_RETRY_DELAY = 1000;

    private final Log logger = LogFactory.getLog(getClass());

    private static final String CONFIGURE_PATH = "/hehua/pubsub";

    private volatile Map<String, String> configure;

    private Map<String, Integer> versionMap = new HashMap<>();

    private ZooKeeper keeper = null;

    private ListMultimap<String, Subscriber> subscribersByKey = ArrayListMultimap.create();

    private ZookeeperPubSubService() {
        boolean init = initWithRetry(INIT_FAIL_RETRY_TIMES, INIT_FAIL_RETRY_DELAY);
        if (!init) {
            throw new RuntimeException("fail to init zookeeper config.");
        }
    }

    public boolean initWithRetry(int maxRetryTimes, long retryDelay) {
        boolean init = false;
        int retryTimes = 0;
        while ((!init) && (retryTimes++) < maxRetryTimes) {
            try {
                configure = reloadAll();
                init = true;
            } catch (Throwable e) {
                logger.debug("Ops. init config from zk err", e);
            }
        }
        return init;
    }

    private static ZookeeperPubSubService _instance = new ZookeeperPubSubService();

    public static ZookeeperPubSubService getInstance() {
        return _instance;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void process(WatchedEvent event) {
        logger.debug("zookeeper event " + event.getType() + ", " + event.getState() + ", "
                + event.getPath());
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected:
                    break;
                case Expired:
                    // TODO 这里应该重新初始化zk
                    logger.error("Zookeeper session expired:" + event);
                    logger.info("re-initializing ZooKeeper");
                    if (!initWithRetry(RELOAD_FAIL_RETRY_TIMES, RELOAD_FAIL_RETRY_DELAY)) {
                        logger.error("Ops. initConfig from zookeeper Failure.");
                    }
                    break;
            }
        } else if (event.getType() == Event.EventType.NodeChildrenChanged
                || event.getType() == Event.EventType.NodeDataChanged) {
            if (StringUtils.startsWith(event.getPath(), CONFIGURE_PATH)) {
                if (!initWithRetry(RELOAD_FAIL_RETRY_TIMES, RELOAD_FAIL_RETRY_DELAY)) {
                    logger.error("Ops. initConfig from zookeeper Failure.");
                } else {
                    logger.debug("zookeeper config changed. reload completed, change:"
                            + event.getPath());
                }
            } else {
                logger.info("zk node changed, but zkconfig no need change.");
            }

        } else if (event.getType() == EventType.NodeCreated) {
            logger.info("need not deal event:" + event);
        } else {
            logger.warn("Unhandled event:" + event);
        }
    }

    /**
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     * 
     */
    private Map<String, String> reloadAll() throws IOException, KeeperException,
            InterruptedException {
        boolean notInit = keeper == null;
        ZooKeeper old = null;
        if (notInit) {
            keeper = ZookeeperClientFactory.getInstance().createClient(this);
        } else {
            boolean oldIsAlive = keeper.getState().isAlive();
            if (!oldIsAlive) {
                old = keeper;
                keeper = ZookeeperClientFactory.getInstance().createClient(this);
            } else {
                old = null;
            }
        }

        logger.debug("reload start");

        List<String> children = keeper.getChildren(CONFIGURE_PATH, true);
        Map<String, String> result = new HashMap<String, String>();
        for (String path : children) {
            Stat stat = new Stat();
            byte[] data = keeper.getData(CONFIGURE_PATH + "/" + path, true, stat);
            String value = new String(data);

            int newVersion = stat.getVersion();
            Integer oldVersion = versionMap.put(path, newVersion);

            if (!notInit) {
                // if oldVersion != newVersion, send notify
                if (oldVersion == null || oldVersion != newVersion) {
                    logger.debug("notify: path=" + path + ", value=" + value + ", ov=" + oldVersion
                            + ",nv=" + newVersion);
                    notify(path, value);
                }
            }
            result.put(path, value);
        }
        try {
            if (old != null) {
                old.close(); // 将旧的关闭掉
                logger.info("success to close expired ZooKeeper:" + old.hashCode());
            }
        } catch (InterruptedException e) {
            logger.error("fail to close expired ZooKeeper:" + old.hashCode(), e);
        }
        return result;
    }

    private void notify(String key, String value) {
        List<Subscriber> subscribers = subscribersByKey.get(key);
        for (Subscriber subscriber : subscribers) {
            try {
                subscriber.onMessage(value);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void set(String path, String value) {
        try {
            Stat stat = new Stat();
            String key = CONFIGURE_PATH + "/" + path;
            keeper.getData(key, null, stat);
            keeper.setData(key, value.getBytes(), stat.getVersion());
        } catch (Throwable e) {
            logger.error("error on set data," + path + "=[" + value + "].", e);
        }
    }

    public void setOrCreate(String path, String value) {
        try {
            String key = CONFIGURE_PATH + "/" + path;
            Stat stat = keeper.exists(key, null);
            if (stat == null) {
                keeper.create(key, value.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                keeper.setData(key, value.getBytes(), stat.getVersion());
            }
        } catch (Throwable e) {
            logger.error("error on set data," + path + "=[" + value + "].", e);
        }
    }

    @Override
    public void register(String key, Subscriber subscriber) {
        subscribersByKey.put(key, subscriber);
    }

    @Override
    public void post(String key, String message) {
        setOrCreate(key, message);
    }

}
