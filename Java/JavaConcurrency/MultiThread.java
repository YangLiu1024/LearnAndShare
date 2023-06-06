import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class MultiThread {
    public static void main(String[] args) throws InterruptedException {
        testDaemonThread();
    }

    private static void testDaemonThread() {
        Thread daemon = new Thread() {
            public void run() {
                try {
                    System.out.println("daemon start");
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println("daemon end");
                } catch (InterruptedException e) {
                    System.out.print("i am interrupted");
                } finally {
                    System.out.print("finalize code is executed");
                }
            }
        };
        //控制台不会打印任何信息
        daemon.setDaemon(true);
        daemon.start();
    }

    private static void testDefaultThreads() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        // thread id: 1 thread name: main, 运行 main 方法的线程
        // thread id: 2 thread name: Reference Handler， 清除据reference 的线程
        // thread id: 3 thread name: Finalizer, 调用对象 finalize 方法的线程        
        // thread id: 4 thread name: Signal Dispatcher
        // thread id: 5 thread name: Attach Listener  
        // thread id: 12 thread name: Common-Cleaner
        for (ThreadInfo info : threadBean.dumpAllThreads(false, false)) {
            System.out.println("thread id: " + info.getThreadId() + " thread name: " + info.getThreadName());
        }
    }

    private static void testThreadMethod() throws InterruptedException {
        TaskQueue q = new TaskQueue();
        ArrayList<Thread> ts = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++) {
            var t = new Thread() {
                public void run() {
                    // 执行task:
                    while (true) {
                        try {
                            String s = q.getTask();
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getName() + "interrupted");
                            return;
                        }
                    }
                }
            };
            t.start();
            ts.add(t);
        }
        Thread add = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                // 放入task:
                String s = "t-" + i;
                System.out.println("add task: " + s);
                q.addTask(s);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        });
        add.start();
        add.join();
        Thread.sleep(1);
        for (Thread t : ts) {
            System.out.println("interrupt task: " + t.getName());
            t.interrupt();
        }
    }
}

class TaskQueue {
    Queue<String> queue = new LinkedList<>();

    public synchronized void addTask(String s) {
        this.queue.add(s);
        // 因为有多个线程在等待 this 锁，则调用 notifyAll, 一次性唤醒所有等待的线程
        this.notifyAll();
    }

    public synchronized String getTask() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + "before wait");
            this.wait();
            System.out.println(Thread.currentThread().getName() + "after wait");
            // 被唤醒后，将继续尝试获取锁
            // 当某一个等待线程t1获取锁后(这个时候 addTask 线程已经执行结束，释放了锁)，此时，判断 queue 是否为空，不为空，则跳出循环
            // 对于其它唤醒的线程，也要尝试获取锁，它们必须等待 t1 getTask 运行结束才能获取到锁，但获取锁后，发现 queue 为空，则又 wait
        }
        String s = queue.remove();
        System.out.println(Thread.currentThread().getName() + " execute task: " + s);
        return s;
    }
}