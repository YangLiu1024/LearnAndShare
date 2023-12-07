package executor.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestExecutor {
    public static void main(String[] args) {
        // cached executor 一般会给每一个任务分配一个线程，在任务结束时，销毁线程
        ExecutorService cached = Executors.newCachedThreadPool();
        cached.execute(new MyTask());
        cached.execute(new MyTask());
        cached.execute(new MyTask());
        cached.shutdown();
        
        // fixed executor 在初始化时就会创建指定数量的线程
        // ExecutorService fixed = Executors.newFixedThreadPool(5);
        // fixed.execute(new MyTask());
        // fixed.execute(new MyTask());
        // fixed.execute(new MyTask());
        // fixed.shutdown();

        // single 和 fixed 类似，但是 single 只有一个线程，且所有的任务需要进行排队，只有当上一个任务结束，下一个任务才可以开始
        // single 可以保证任务序列进行
        // ExecutorService single = Executors.newSingleThreadExecutor();
        // single.execute(new MyTask());
        // single.execute(new MyTask());
        // single.execute(new MyTask());
        // single.shutdown();
    }

    public static final class MyTask implements Runnable {
        private static int count = 0;

        private final int id = count++;

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println(String.format("task id: %d current index: %d thread name: %s", id, i, Thread.currentThread().getName()));
                // yield 是当前线程通知 调度器，自己可以失去处理器的使用时间了。但是具体调用还是调度器自己决定。
                // sleep 是当前线程要 阻塞指定时间，调度器调用其它线程
                // 区别在于 yield 的线程是直接进入 ready pool, sleep 是进入 阻塞队列，需要等待调度器唤醒。
                // 两者相同之处在于，都不涉及到 lock. 即线程在 yield/sleep 后并不会丢失任何 monitor 的 ownership.
                // by the way, object.wait() 就会导致当前线程丢失 object 的 ownership
                Thread.yield();
            }
            System.out.println(id + " finished");
        }

         
    }
}
