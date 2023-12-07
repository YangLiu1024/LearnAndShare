package executor.code;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestException {
    public static final class MyTask implements Runnable {

        @Override
        public void run() {
            throw new UnsupportedOperationException("Unimplemented method 'run'");
        }

    }

    public static void main(String[] args) {
        ExecutorService single = Executors.newSingleThreadExecutor();
        // 一般来说, 异常是不可以跨线程传递的, 如果在线程里发生了未被捕获的异常,该异常会被打印到 console
        single.execute(new MyTask());
        // Java SE5 提供了一个办法来解决这个问题, 即 Thread.UncaughtExceptionHandler, 它允许你为一个线程,指定一个异常处理器
        // 即当线程出现了未被捕获的异常面临死亡时,则会调用该处理器
        single.shutdown();
    }
}
