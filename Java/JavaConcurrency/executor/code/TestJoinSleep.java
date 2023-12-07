package executor.code;

public class TestJoinSleep {
    public static final class Sleeper extends Thread {
        private final int duration;
        public Sleeper(int duration, String name) {
            super(name);
            this.duration = duration;
            start();
        }
        @Override
        public void run() {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                System.out.println("Sleeper: " + getName() + " is interrupted by other thread. isInterrupted(): " + isInterrupted());
                return;
            }
            System.out.println("Sleeper: " + getName() + " is aweakend");
        }        

    }

    public static final class Joiner extends Thread {
        private Sleeper sleeper;
        public Joiner(Sleeper sleeper, String name) {
            super(name);
            this.sleeper = sleeper;
            start();
        }

        @Override
        public void run() {
            try {
                sleeper.join();
            } catch (InterruptedException e) {
                System.out.println("Joiner: " + getName() + " is interrupted by other thread. isInterrupted(): " + isInterrupted());
                return;
            }
            System.out.println("Joiner: " + getName() + " complete");
        }       
    }

    public static void main(String[] args) {
        Sleeper s1 = new Sleeper(2000, "s1");
        Sleeper s2 = new Sleeper(1500, "s2");

        Joiner j1 = new Joiner(s1, "j1");
        Joiner j2 = new Joiner(s2, "j2");
        // 如果没有任何线程被 interrupt, 那么 程序 就会是 s2 醒来, j2 join 成功, s1 醒来, j1 join 成功

        // 尝试 打断 s2, s2 就会中断睡眠,然后抛出 InterruptedException 异常. 但可以观察到 isInterrupted() 返回为 false
        // 这是因为在 调用 s2.interrupt() 的时候, s2 的中断标志位被置为 true, 在异常被捕获的时候,这个标志位会被清理为 false
        s2.interrupt();

        // 尝试中断 j1, j1 就会中断 等待,但 s1 会继续 睡眠,直到结束被唤醒.
        j1.interrupt();
    }
}
