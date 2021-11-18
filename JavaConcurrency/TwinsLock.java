import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class TwinsLock {
    private static class Sync extends AbstractQueuedSynchronizer {
        public Sync(int count) {
            setState(count);
        }

        protected int tryAcquireShared(int acquireCount) {
            for (;;) {
                int state = getState();
                int newCount = state - acquireCount;
                if (newCount < 0 || compareAndSetState(state, newCount)) {
                    return newCount;// 如果返回一个负数，则当前线程将阻塞并进入同步队列，对于独占式，则返回 boolean 状态
                }
            }
        }

        protected boolean tryReleaseShared(int returnCount) {
            for (;;) {
                int state = getState();
                int newCount = state + returnCount;
                if (compareAndSetState(state, newCount)) {
                    return true;
                }
            }
        }
    }

    private Sync sync = new Sync(2);

    public void lock() {
        sync.acquireShared(1);
    }

    public void unlock() {
        sync.releaseShared(1);
    }

    public static void main(String[] args) throws InterruptedException {
        TwinsLock lock = new TwinsLock();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new Worker(lock));
            t.setDaemon(true);
            t.start();
        }

        for (int i = 0; i < 10; i++) {
            Thread.currentThread().sleep(1000);
        }

    }

    private static class Worker implements Runnable {
        private volatile boolean running = true;
        private TwinsLock lock;

        public Worker(TwinsLock lock) {
            this.lock = lock;
        }

        public void run() {
            while (running) {
                lock.lock();// 尝试获取共享锁，如果失败，则会阻塞
                try {
                    long time = Math.round(Math.random() * 2000);
                    System.out.println(Thread.currentThread().getName() + "acquired lock, sleeped " + time);
                    Thread.currentThread().sleep(time);

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    System.out.println(Thread.currentThread().getName() + "unlocked ");
                    lock.unlock();
                    try {
                        Thread.currentThread().sleep(1000);//unlock 之后给其它线程机会来争夺锁，否则很可能被当前线程继续争夺成功
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}