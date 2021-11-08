# Java 多线程
在多核或者单核系统，都支持多线程执行。系统操作的是线程而不是进程，且系统会为每个线程设置时间片，当线程运行满了时间片，或者自身挂起了，比如访问 IO 资源等，那么系统就会进行线程之间的切换。  
线程之间的切换是有消耗的，CPU 需要先记录线程当前状态，在该线程重新运行时，重新加载该状态已继续运行。所以并不是多线程就一定比单线程运行更快。  
对于 Java 程序来讲，JVM 运行时就会创建一个进程，且用一个主线程来运行 main 方法。同时，还可以在 main 方法里创建其它子线程，JVM 自己本身也有一些工作线程，比如垃圾回收线程。

## 创建线程
在 java 里创建线程很简单， *new Thread()* 即可， *Thread* 类会实现 *run* 方法。 当线程运行时，即会执行 *run* 方法
```js
public class Demo {
    public static void main() {
        System.out.println('main start');
        Thread t = new Thread(() -> {
            System.out.println('thread start');
            System.out.println('thread end');
        })
        t.start()
        System.out.println('main end');
    }
}
```
*start* 方法只能调用一次，且只会在调用该方法后，系统才会真的创建一个线程。  
线程在创建后，它的执行顺序由系统调度，代码并不能控制。  
比如，对于上例，我们只能确定 *main start* 必然最先打印，*thread start* 也肯定在 *thread end* 之前，至于 *main end* 在 *thread start* 之前，或者 *thread start* 与 *thread end* 之间，抑或在 *thead end* 之后，都是有可能的。这都取决于 CPU 的 调度。

## 线程状态
Java 线程在运行的生命周期中给定时刻，只会处于下列状态中的其中一个。
线程在创建后，处于 *New* 状态，这个时候还没有执行 *start* 方法。表示初始状态  
在线程就绪或执行 *run* 方法时，状态为 *runable*.表示运行状态  
如果线程阻塞于 synchronized 锁， 状态为 *Blocked*, 表示阻塞状态  
如果线程需要等待其它线程做出一些特定动作(通知或中断)，比如 t1.wait(), t2.notify(), 状态为 *waiting*, 表示等待状态  
如果线程在等待后可以自动返回，比如 t.sleep(), 状态为 *Time Waiting*, 表示超时等待状态
线程在执行完 *run* 方法或者抛出未捕获异常终止后，状态改为 *Terminated*. 表示终止状态

## 线程中断
如果一个线程消耗了太多时间还没有结束，比如网速太慢，下载一直没有完成，则用户可以 cancel 该任务，即中断下载线程。  
在 Java 里，中断线程可以使用 *t.interrupt()* 方法，表示在当前线程中去中断 t 线程。  
而每个线程有一个标志位来记录当前线程是否被中断, 线程可以通过 *inInterrupted* 来检查自己是否被中断。但注意，如果一个线程已经终结，即使它被中断过，*isInterrupted* 也会返回 false
```js
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new MyThread();
        t.start();
        Thread.sleep(1000);
        t.interrupt(); // 中断t线程
        t.join(); // 等待t线程结束
        System.out.println("end");
    }
}

class MyThread extends Thread {
    public void run() {
        Thread hello = new HelloThread();
        hello.start(); // 启动hello线程
        try {
            hello.join(); // 等待hello线程结束, 且如果当前线程被中断，则 hello.join 会抛出 InterruptedException 异常
        } catch (InterruptedException e) {
            System.out.println("interrupted!");
        }
        //运行到这里，表示 hello 已经正常结束，或者当前线程被中断
        //对于本例，hello 线程不可能正常结束，比如对它也 interrupt, 才能终止该 hello 线程，否则它会一直运行下去， JVM 不会退出
        hello.interrupt();
    }
}

class HelloThread extends Thread {
    public void run() {
        int n = 0;
        while (!isInterrupted()) {
            n++;
            System.out.println(n + " hello!");
            try {
                Thread.sleep(100);//如果当前线程被中断，则该方法抛出 InterruptedException
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
```
*t.interrupt* 会给线程 t 发送中断请求。如果 t 线程本身已经处于等待状态，则该线程会抛出 InterruptedException 异常。  
注意在抛出异常前，JVM 会清除中断标志，此时若检查 *isInterrupted*, 会返回 false.  
更安全的中断线程的方法不是通过 *interrupt* 方法，而是通过 Boolean 变量来控制线程是否需要继续运行。 

## 守护线程
JVM 运行时，主线程可以创建多个子线程，当所有线程都运行结束时，JVM 才能退出，进程结束。  
如果有一个线程没有结束，则 JVM 不会退出，进程不能结束。  
但是在有的时候，有的线程本身就是想要无限循环，比如计时线程，也没有其它线程来负责结束该线程，这个时候该怎么办呢？  
答案是创建守护线程，在线程 start 之前， 调用 *t.setDaemon(true)*  
当所有工作线程都结束后，JVM 无论有没有守护线程，都会自动退出。退出时，所有守护线程都需要立即终止，但是JVM 并不保证守护线程退出时，它的 finally 代码块一定会被执行。  

## 线程同步
当多线程同时运行时，线程的调度由系统决定，程序本身无法决定。因此，任何一个线程都可能在任意指令处暂停，然后在某个时间段后继续执行。  
这个时候，就会带来一个问题，当多个线程同时读写共享变量时，会出现数据不一致的问题。
```js
public class Demo {
    public static void main(String[] args) throws Exception {
        var add = new AddThread();
        var dec = new DecThread();
        add.start();
        dec.start();
        add.join();
        dec.join();
        System.out.println(Counter.count);
    }
}

class Counter {
    public static int count = 0;
}

class AddThread extends Thread {
    public void run() {
        for (int i=0; i<10000; i++) { Counter.count += 1; }
    }
}

class DecThread extends Thread {
    public void run() {
        for (int i=0; i<10000; i++) { Counter.count -= 1; }
    }
}
```
最后的结果 *Counter.count* 看起来应该是 0, 但每次运行的结果都可能不一样。这是因为 *Counter.count += 1* 本身并不是原子操作，它对应了三条指令：*LOAD, ADD, STORE*, 即读取，加，写回， CPU 可能在任意指令处暂停该线程。  
比如当前 *count=100*, *Add* 线程读取 100 暂停, Dec 线程也读取 100, 然后写回 99. 接着 *Add* 线程又写回 101, 那么 Dec 线程的自减就相当于没有效果，导致最后的结果出错。  
为了让加减操作变成原子操作，Java 提供了 *synchronized* 关键字。 该关键字会将包裹的代码块边界加上锁，任何想要访问该代码块的线程，都必须拿到该锁，才能执行代码块里面的代码。且该关键字保证了无论怎样退出代码块，即使抛出了未被捕获的异常， 锁都会被释放。  
对于上例，如果 add 线程拿到了锁，但并没有执行完代码块，锁并没有释放，如果这个时候 dec 线程也想拿到锁，但是锁已经被占用，那么 dec 线程则只能等待锁释放。
```js
public class Demo {
    public static void main(String[] args) throws Exception {
        var add = new AddThread();
        var dec = new DecThread();
        add.start();
        dec.start();
        add.join();
        dec.join();
        System.out.println(Counter.count);
    }
}

class Counter {
    public static final Object lock = new Object()
    public static int count = 0;
}

class AddThread extends Thread {
    public void run() {
        for (int i=0; i<10000; i++) {
            synchronized(Counter.lock) {
                Counter.count += 1;
            }
        }
    }
}

class DecThread extends Thread {
    public void run() {
        for (int i=0; i<10000; i++) {
            synchronized(Counter.lock) {
                Counter.count -= 1; 
            }
        }
    }
}
```
这个时候，执行加减操作之前，当前线程都必须先拿到锁 *Counter.lock*, 这就保证了加减操作的原子性。  
Java 里，锁可以是任意对象，锁的信息存储在对象里。它可以记录当前锁是否已经赋予了某个线程以及该锁赋给该线程的次数。比如一个对象在同步方法里调用了另一个同步方法，则锁会被赋予该线程两次。  
*synchronized* 可以直接修饰对象，比如 *synchronized(lock)*, 也可以用于修饰对象方法和类方法。  
修饰对象方法时，表示用调用该方法的对象作为锁，任意其它持有该对象的线程在访问任意同步方法时，都要尝试获取该对象的锁。如果锁已经被其它线程占用，则必须等待。  
修饰类方法时，使用该类的 *.class* 对象作为锁。这样，任意对该同步类方法的调用都必须获取类的 *.class* 对象锁。  

### 不需要 synchronized 的操作
JVM 规范定义了几种原子操作
* 基本类型的赋值，比如 int n = m, 但 64位的 double/long 除外， JVM 并没有作明确规定。
* 引用类型的赋值, 比如 String s = a

## wait/notify
*synchronized* 解决了线程竞争的问题，但是对于线程之间的协调合作，则没有办法。  
比如，我们有一个任务队列 *TaskQuene*, 一个线程可以往里面添加任务，一个线程可以一直轮询该队列，如果存在任务，则返回任务，如果不存在，则一直循环等待。    
```js
class TaskQueue {
    Queue<String> queue = new LinkedList<>();

    public synchronized void addTask(String s) {
        this.queue.add(s);
    }

    public synchronized String getTask() {
        while (queue.isEmpty()) {
        }
        return queue.remove();
    }
}
```
上面的代码仍然有问题，因为一旦一个线程进入 getTask 方法，则必然已经获取了该对象的锁，任意其它线程已经没有办法再进入 addTask 方法。  
为了解决上面的问题，我们应该让 getTask 线程在条件不满足时等待，释放对应的锁。  
```js
    public synchronized String getTask() {
        while (queue.isEmpty()) {
            //object.wait() 必须在持有该 object 作为锁的线程中调用
            //如果调用该方法的线程没有持有 object 的锁，则抛出 IllegalMonitorStateException 异常
            //成功调用后，该锁会与当前线程解绑，锁资源被释放，进入 wating 状态，线程被挂起，不会被 CPU 调度
            //直到被其它线程唤醒，或者被 interrupt, wait 方法才会返回，且当前线程又会尝试获取锁，然后继续执行后面的代码
            this.wait();
        }
        return queue.remove();
    }
```
所以，*wait* 方法只能在同步代码块里调用，且只能被锁对象调用。wait(long) 也可以指定等待时长，单位为毫秒，若超过时长，则自动返回。    
接下来，剩下的问题，就是怎么通知 wait 的线程。  
```js
public synchronized void addTask(String s) {
    this.queue.add(s);
    //notify 是将waiting 队列里的某个线程移动到 entry 队列，状态由 wating 改为 blocked
    //notifyAll 是将 wating 队列里所有线程移到 entry 队列
    //这些唤醒的线程需要重新竞争锁，竞争成功，才能从 wait 方法返回，继续执行之后的代码，竞争失败，继续 blocked
    this.notify(); // 唤醒在this锁等待的线程
}
```
以下是一个完整的例子
```js
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        var q = new TaskQueue();
        var ts = new ArrayList<Thread>();
        for (int i=0; i<5; i++) {
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
        var add = new Thread(() -> {
            for (int i=0; i<10; i++) {
                // 放入task:
                String s = "t-" + i;
                System.out.println("add task: " + s);
                q.addTask(s);
                try { Thread.sleep(100); } catch(InterruptedException e) {}
            }
        });
        add.start();
        add.join();
        Thread.sleep(1);
        for (var t : ts) {
            System.out.println("interrupt task: " + t.getName());
            t.interrupt();
        }
    }
}

class TaskQueue {
    Queue<String> queue = new LinkedList<>();

    public synchronized void addTask(String s) {
        this.queue.add(s);
        //因为有多个线程在等待 this 锁，则调用 notifyAll, 一次性唤醒所有等待的线程
        this.notifyAll();
    }

    public synchronized String getTask() throws InterruptedException {
        //这里必须使用 while 而不是 if
        //因为存在多个 getTask 的线程，queue 里的元素被其中一个线程消耗了，对其它唤醒的线程，仍然需要继续check 是否为空， 否则 queue.remove() 会报错
        while (queue.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + "before wait");
            this.wait();
            System.out.println(Thread.currentThread().getName() + "after wait");
            //被唤醒后，将继续尝试获取锁
            //当某一个等待线程t1获取锁后(这个时候 addTask 线程已经执行结束，释放了锁)，此时，判断 queue 是否为空，不为空，则跳出循环
            //对于其它唤醒的线程，也要尝试获取锁，它们必须等待 t1 getTask 运行结束才能获取到锁，但获取锁后，发现 queue 为空，则又 wait
        }
        String s = queue.remove();
        System.out.println(Thread.currentThread().getName() + " execute task: " + s);
        return s;
    }
}
```
## volatile
*synchronized* 可以解决代码块的同步，但是它比较重量级，会因为竞争锁引起线程之间的切换。  
*volatile* 是轻量级的 *synchronized*, 它能保证共享变量在多线程之间的可见性，且不需要切换线程。  
当一个线程对 *volatile* 变量进行写操作时，会将 volatile 变量对应的缓存行写回主存，并对其它线程广播，该缓存行已经失效。 当其它线程接收到该消息，会将该缓存行标记为失效。那么该线程如果接下来要读取该变量，则会重新从主存读取最新的缓存行。这就是缓存一致性。缓存一致性也会阻止同时修改由两个以上处理器缓存的内存区域数据。 