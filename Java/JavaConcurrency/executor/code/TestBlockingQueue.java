package executor.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.FutureTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class TestBlockingQueue {

    static class MyTask implements Runnable {
        private String id;
        public MyTask(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("start " + id);
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println("finish " + id);
            } catch (InterruptedException e) {
                System.out.println(id + " is interrupted");
                Thread.currentThread().interrupt();
            }
            
        }
    }

    static final class TaskGenerator implements Runnable {
        private BlockingQueue<MyTask> queue;
        private int count = 0;
        public TaskGenerator(BlockingQueue<MyTask> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    for (int i= 0; i< 3; i++) {
                        queue.put(new MyTask("generate " + count++));
                    }
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch (InterruptedException e) {
                System.out.println( "generator is interrupted");
            }
        }
        
    }
    static final class MyRunner implements Runnable {
        private BlockingQueue<MyTask> queue;
        private String name;
        public MyRunner(String name, BlockingQueue<MyTask> queue) {
            this.queue = queue;
            this.name = name;
        }

        public void add(MyTask element) {
            try {
                this.queue.put(element);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(name + " add task is interrupted");
            }
        }

        @Override
        public void run() {
            try {
                while(!Thread.interrupted()) {
                    MyTask e = this.queue.take();
                    e.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(name + " is interrupted");
            }
  
        }
        
    }

    static void waitInput(String name) {
        System.out.println("waiting " + name);
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {

        }
    }

    static void test(String name, BlockingQueue<MyTask> queue) {
        MyRunner runner = new MyRunner(name + 1, queue);
        Thread t = new Thread(runner);
        t.start();

        MyRunner runner2 = new MyRunner(name + 2, queue);
        Thread t3 = new Thread(runner2);
        t3.start();

        Thread t2 = new Thread(new TaskGenerator(queue));
        t2.start();
        for (int i=0; i < 5; i++) {
            runner.add(new MyTask(name + i));
        }
        waitInput(name);
        t.interrupt();
        t2.interrupt();
        t3.interrupt();
        System.out.println(name + " is finished");
    }

    public static void main(String[] args) {
        //test("LinkedBlockingQueue", new LinkedBlockingDeque<>());
        // test("ArrayBlockingQueue", new ArrayBlockingQueue<>(3));
        System.out.println(Arrays.asList(2, 4, 6, 3, 1).stream().sorted((i1, i2) ->  i1 - i2).findFirst().orElse(-1));
    }
    
}
