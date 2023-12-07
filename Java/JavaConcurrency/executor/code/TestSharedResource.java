package executor.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSharedResource {

    public static abstract class IntGenerator {
        private volatile boolean canceled = false;

        public void cancel() {
            this.canceled = true;
        }

        public boolean isCancelled() {
            return this.canceled;
        }

        public abstract int next();

    }

    public static final class EvenChecker implements Runnable{
        private IntGenerator g;
        public EvenChecker(IntGenerator g) {
            this.g = g;
        }
        @Override
        public void run() {
            while(!g.isCancelled()) {
                int v = g.next();
                if (v % 2 != 0) {
                    g.cancel();
                    System.out.println("invalid number " + v);
                }
            }
        }
        

    }

    public static final class EvenGenerator extends IntGenerator {
        private int value = 0;
        // 给 value 加上 volatile 并不会有什么作用，next 仍然不是原子性操作，只能保证在执行一次 ++value 后，这次改动会被后续的读操作所获知
        //private volatile int value = 0;

        // Java 为了解决原子性的问题，引入了锁，比如最常见的 synchronized, synchronized 可以修饰成员方法，可以修饰对象，可以修饰静态方法
        // 不管修饰什么，其本质就是 线程持有锁对象。当某一个线程持有了锁后，其它线程想获取相同的锁，则必须等待该线程释放锁。
        // @Override
        // public int next() {
        //     ++value;
        //     ++value;
        //     return value;
        // }

        @Override
        public synchronized int next() {
            ++value;
            ++value;
            return value;
        }
        
    }
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        EvenGenerator g = new EvenGenerator();

        // test1
        // invalid number 13579
        // invalid number 13579
        // invalid number 13575
        // invalid number 13577
        // 为什么出出现多个打印结果，且为什么数字会为奇数, 还有的数字相同？
        // 之所以可能出现多个打印结果，是因为在某一个线程发出 cancel 信号时，其它多个线程已经进入了 while 函数体
        // 会出现奇数，是因为 generator 的 next 本身并不是一个原子性操作，线程可能会在中间步骤挂起
        for (int i = 0; i < 10; i ++) {
            pool.execute(new EvenChecker(g));
        }
        pool.shutdown();
    }
}
