import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {
    public static final CountDownLatch start = new CountDownLatch(1);
    public static CountDownLatch end;
    public static final ConnectionPool pool = new ConnectionPool(10);

    private ArrayList<Runnable> jobs = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 20;// try 10, 20, 30, 40, 50, more threads, less success rate
        end = new CountDownLatch(threadCount);

        int count = 20;
        AtomicInteger got = new AtomicInteger(0);
        AtomicInteger nogot = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new Runner(count, got, nogot));
            t.start();
        }
        start.countDown();
        end.await();// 保证所有线程运行完毕
        System.out.println("got: " + got.get() + " nogot: " + nogot.get());
    }

    //线程池一般会创建多个线程，每个线程绑定一个 Worker, 线程在创建的时候就 start
    //Worker 通过 running 标志来判断是否还需要工作，如果需要，则一直尝试从 jobs 里获取 job
    //客户端只需要往线程池里添加 job, 就可以直接返回。线程池在添加 job 到 jobs 里后，一般需要调用 notify 来通知等待 job 的 线程
    public class Worker implements Runnable {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                synchronized (jobs) {
                    if (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        Runnable job = jobs.remove(0);
                        job.run();
                    }
                }
            }
        }

        public void shutdown() {
            this.running = false;
        }
    }


    public static class Runner implements Runnable {
        int count;
        AtomicInteger got;
        AtomicInteger nogot;

        public Runner(int count, AtomicInteger got, AtomicInteger nogot) {
            this.count = count;
            this.got = got;
            this.nogot = nogot;
        }

        @Override
        public void run() {
            try {
                start.await();//确保所有线程同时开始获取 connection
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (count > 0) {
                try {
                    Connection conn = pool.fetch(1000);
                    if (conn == null) {
                        nogot.incrementAndGet();
                    } else {
                        try {
                            Thread.sleep(100);
                        } finally {
                            got.incrementAndGet();
                            pool.release(conn);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
             }
             end.countDown();
        }
        
    }
    public static class ConnectionPool {
        ArrayList<Connection> pool = new ArrayList<>();

        public ConnectionPool(int size) {
            for (int i = 0; i < size; i++) {
                pool.add(new Connection());
            }
        }

        public Connection fetch(long millis) throws InterruptedException {
            synchronized(pool) {
                if (!pool.isEmpty()) {
                    return pool.remove(0);
                } else {
                    long remain = millis;
                    long future = System.currentTimeMillis() + millis;
                    while (remain > 0 && pool.isEmpty()) {
                        pool.wait(remain);
                        remain = future - System.currentTimeMillis();
                    }
                    return pool.isEmpty() ? null : pool.remove(0);
                }
            }
        }

        public void release(Connection conn) {
            synchronized(pool) {
                pool.add(conn);
                pool.notifyAll();
            }
        }
    }

    public static class Connection {

    }
}