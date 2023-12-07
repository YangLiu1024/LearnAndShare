package executor.code;

import java.util.concurrent.TimeUnit;

public class TestInterrupt {
    public static final class NeedsCleanup {
        private final int id;
        public NeedsCleanup(int id) {
            this.id = id;
            System.out.println("Need clean up " + id);
        }

        public void cleanup() {
            System.out.println("clean up " + this.id);
        }
    }

    public static final class Blocked implements Runnable {

        private volatile double d = 0.0;
        @Override
        public void run() {
            try {
               while (!Thread.interrupted()) {
                    NeedsCleanup n1 = new NeedsCleanup(1);
                    try {
                        System.out.println("sleeping ");
                        TimeUnit.MILLISECONDS.sleep(100);

                        NeedsCleanup n2 = new NeedsCleanup(2);
                        try {
                            System.out.println("calculating ");
                            for (int i = 0; i < 2500000; i++) {
                                d = d + (Math.PI + Math.E) / d;
                            }
                            System.out.println("finish calculating ");
                        } finally {
                            n2.cleanup();
                        }

                    } finally {
                        n1.cleanup();
                    }
               }
               System.out.println("exit while loop ");
            } catch (InterruptedException e) {
                System.out.println("exit interrupted ");
            }
        }
        
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new Blocked());
        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
    }
}
