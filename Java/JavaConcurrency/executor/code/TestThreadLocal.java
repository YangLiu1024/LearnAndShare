package executor.code;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestThreadLocal {

    public static final class ThreadLocalHolder {
        private static ThreadLocal<Integer> value = new ThreadLocal<Integer>(){
            private Random random = new Random(47);
            protected synchronized Integer initialValue() {
                return random.nextInt(10000);
            }
        };
        public static void increase() {
            value.set(value.get() + 1);
        }

        public static Integer get() {
            return value.get();
        }
    }

    public static final class Accessor implements Runnable {
        private int id;

        public Accessor(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                ThreadLocalHolder.increase();
                System.out.println(this);
                Thread.yield();
            }
        }

        public String toString() {
            return "id " + this.id + ": " + ThreadLocalHolder.get();
        }

        
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(new Accessor(i));
        }
        TimeUnit.SECONDS.sleep(3);
        exec.shutdownNow();// 对线程池里的所有线程调用 interrupt()
    }
}
