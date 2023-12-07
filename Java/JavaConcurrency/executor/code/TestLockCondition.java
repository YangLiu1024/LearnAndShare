package executor.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLockCondition {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean ready = false;

    public void step1() {
        lock.lock();
        try {
            ready = true;
            System.out.println(Thread.currentThread().getName() + " step1");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void step2() {
        lock.lock();
        try {
            ready = false;
            System.out.println(Thread.currentThread().getName() + " step2");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitForStep2() {
        lock.lock();
        try {
            while (ready == true) {
                System.out.println(Thread.currentThread().getName() + " wait for step2");
                condition.await();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " is interrupted");
            //Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void waitForStep1() {
        lock.lock();
        try {
            while (ready == false) {
                System.out.println(Thread.currentThread().getName() + " wait for step1");
                condition.await();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " is interrupted");
            //Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static final class Step1 implements Runnable {
        private final TestLockCondition t;
        public Step1(TestLockCondition t) {
            this.t = t;
        }

        @Override
        public void run() {
            // 对于线程1，如果在 step1 里响应中断，程序仍然会进入 waitForStep2(), 此时线程 interrupted 标志为 true, 但是没有抛出 InterruptedException
            // 然后在 waitForStep2 里进入等待状态

            // 如果在 waitForStep2 里响应中断，会抛出异常，如果不调用 Thread.currentThread().interrupt(); 复位，程序仍然会进入下一次迭代
            while (!Thread.interrupted()) {
                t.step1();
                t.waitForStep2();
            }
            System.out.println(Thread.currentThread().getName() + " is interrupted, exit run");
        }
        
    }

    
    public static final class Step2 implements Runnable {
        private final TestLockCondition t;
        public Step2(TestLockCondition t) {
            this.t = t;
        }

        @Override
        public void run() {
            // 对于线程2，它先是等待 ready, 如果在等待过程中响应中断，则会处理 InterruptedException. 
            // 如果不调用 Thread.currentThread().interrupt(); 来复位，那么该线程的下一次 Thread.interrupted() 仍然会返回 false
            // 那么又会执行 waitForStep1 进入 等待状态。此时就需要看线程1的运行状况来决定之后的运行状况。

            // 如果不是在 waitForStep1 中响应中断，而是在 step2 里，那么则会在处理完 step2 后，Thread.interrupted() 会 返回 true, 线程2 结束运行
            while (!Thread.interrupted()) {
                t.waitForStep1();
                t.step2();
            }
            System.out.println(Thread.currentThread().getName() + " is interrupted, exit run");
        }       
    }

    // case1, 线程1 运行 step1, 线程2 正在 waitForStep1
    public static void main(String[] args) throws InterruptedException {
        TestLockCondition t = new TestLockCondition();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(new Step1(t));
        exec.submit(new Step2(t));

        TimeUnit.SECONDS.sleep(1);
        exec.shutdownNow();
    }
}
