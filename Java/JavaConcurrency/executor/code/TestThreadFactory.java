package executor.code;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TestThreadFactory implements ThreadFactory {

    private Random rand = new Random();
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        //t.setDaemon(true);
        int p = rand.nextInt(3);
        switch(p) {
            case 0: 
                t.setPriority(Thread.MAX_PRIORITY);
                break;
            case 1:
                t.setPriority(Thread.NORM_PRIORITY);
                break;
            case 2:
                t.setPriority(Thread.MIN_PRIORITY);
                break;
        }
        return t;
    }
    
    public static final class MyTask implements Runnable {

        public static int fib(int n) {
            if (n < 2 ) return 1;
            return fib(n - 1) + fib(n - 2);
        }

        private int n;
        private volatile int sum = 0;
        public MyTask(int n) {
            this.n = n;
        }

        @Override
        public void run() {
            for (int i = n; i >= 0; i--) {
                System.out.println(Thread.currentThread().getName() + " start " + i);
                sum += fib(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " is interrupted.");
                } finally {
                    System.out.println(Thread.currentThread().getName() + Thread.currentThread().isDaemon() +  " finally ");
                }
            }
        }
        
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService cached = Executors.newCachedThreadPool(new TestThreadFactory());
        for (int i = 0; i < 5; i++) {
            cached.execute(new MyTask(10));
        }
        cached.shutdown();
        System.out.println("main finished");
        // add some delay to allow executor start to work
        // 因为 executor thread factory 创建的都是 daemon 线程，所以在主线程结束的时候，daemon 线程会被立即终止，其 finally 代码块并不会被执行
        TimeUnit.MILLISECONDS.sleep(150);
    }

}
