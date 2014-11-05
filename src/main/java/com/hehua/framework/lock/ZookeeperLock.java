/**
 * 
 */
package com.hehua.framework.lock;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.hehua.framework.zookeeper.ZookeeperClientFactory;

/**
 * @author zhihua
 *
 */
public class ZookeeperLock implements Lock, Watcher {

    private static final Log logger = LogFactory.getLog(Lock.class);

    private static final String CONFIG_PATH = "/hehua/lock";

    private String lockId;

    private ZooKeeper zk;

    private void initZookeeper() {
        if (zk != null) {
            return;
        }

        try {
            zk = ZookeeperClientFactory.getInstance().createClient(this);
        } catch (IOException e) {
            logger.error("Ops", e);
            throw new RuntimeException(e);
        }
    }

    private void closeZookeeper() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            logger.error("Ops", e);
            throw new RuntimeException(e);
        }
    }

    private boolean lock;

    private String createPath;

    private CountDownLatch latch;

    private String getLockName() {
        return "lock-" + lockId;
    }

    private void getLock() {
        initZookeeper();
        try {
            createPath = zk.create(CONFIG_PATH + getLockName(), new byte[0], Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            latch = new CountDownLatch(1);
            isLock();
            latch.await();
        } catch (KeeperException e) {
            logger.error("Ops", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error("Ops", e);
            throw new RuntimeException(e);
        }
    }

    private int getPathId(String path) {
        return Integer.parseInt(path.substring("/lock-".length()));
    }

    private void isLock() {

        if (lock) return;

        try {

            int createPathId = getPathId(createPath);
            boolean isMin = true;
            List<String> childrens = zk.getChildren(CONFIG_PATH, true);
            for (String children : childrens) {
                int childrenPathId = getPathId(children);
                if (createPathId < childrenPathId) {
                    isMin = false;
                    break;
                }
            }
            if (isMin) {
                lock = true;
                latch.countDown();
                return;
            }
        } catch (KeeperException e) {
            logger.error("Ops", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error("Ops", e);
            throw new RuntimeException(e);
        }

    }

    private void releaseLock() {
        closeZookeeper();
    }

    @Override
    public void process(WatchedEvent event) {
        logger.info(String.format("event: %s - %s - %s", event.getType().name(), event.getPath(),
                event.getState().name()));
        //TODO 
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected:
                    break;
                case Expired:
                    break;
                case Disconnected:
                    break;
            }
        }

        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            isLock();
        }

    }

    private ReentrantLock localLock = new ReentrantLock();

    @Override
    public void lock() {
        localLock.lock();
        getLock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // TODO Auto-generated method stub
        lock();
    }

    @Override
    public boolean tryLock() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void unlock() {
        releaseLock();
        localLock.unlock();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

}
