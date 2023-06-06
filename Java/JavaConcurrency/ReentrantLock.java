import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class ReentrantLock {
    private static class Sync extends AbstractQueuedSynchronizer {
        public boolean tryRelease(int count) {
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new UnsupportedOperationException();
            }
            int newCount = getState() - count;
            boolean free = false;
            if (newCount == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(newCount);
            return free;
        }
    }

    private static final class FairSync extends Sync {
        public void lock() {
            acquire(1);
        }

        protected boolean tryAcquire(int count) {
            Thread t = Thread.currentThread();
            int state = getState();

            if (state == 0) {//公平和非公平的区别就在于，公平锁会检查同步队列里是否已经有了排在自己前面的线程，而非公平锁则是直接尝试获取锁
                if (!hasQueuedPredecessors() && compareAndSetState(0, count)) {
                    setExclusiveOwnerThread(t);
                    return true;
                }
            } else if (t == getExclusiveOwnerThread()) {
                int nc = state + count;
                setState(nc);
                return true;
            }
            return false;
        }
    }

    private static final class NonfairSync extends Sync {
        public void lock() {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
            } else {
                acquire(1);
            }
        }

        protected boolean tryAcquire(int count) {
            Thread t = Thread.currentThread();
            int state = getState();

            if (state == 0) {
                if (compareAndSetState(0, count)) {
                    setExclusiveOwnerThread(t);
                    return true;
                }
            } else if (t == getExclusiveOwnerThread()) {
                int nc = state + count;
                setState(nc);
                return true;
            }
            return false;
        }
    }
}