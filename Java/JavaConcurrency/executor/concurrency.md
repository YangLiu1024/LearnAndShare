# 并发
并发是指使用多线程来共享 CPU 时间，从而使得多个任务，可以轮询执行，看起来像是多个任务同时执行一样。  
如果是多处理器，不同的任务确实是同时执行。 

并发的代价在于在线程切换时，上下文的存储，和重新加载的消耗。如果频繁发生线程的切换，其性能可能反而不如单线程。  

## 任务、线程、线程池
```java
// Runnable 通常被用于定义 task, 任务会被分配给一个线程去执行  
// Runnable 本身并无线程能力，所以需要把 runnable 显式的附着到线程上 
interface Runnable {
    void run()
}

// 传统的将任务附着给线程，是使用线程 Thread 的构造方法, 然后调用线程的 start 方法来启动线程，线程将会去执行 runnable
class Thread {
    Thread(Runnable runnable)
}

// 开发者自己创建，管理线程对象是很麻烦的事情，所以 JDK 提供了 线程池，它在开发者和任务执行之间作为中间层，帮助开发者执行任务，且管理线程对象的生命周期。
class ExecutorService {
    // 停止接收新任务，在调用 shutdown 之前接收的任务仍将被执行
    void shutdown();

    // 执行 runnable task
    void execute(Runnable task);

    // 执行 Callable task, 返回 Future<T>
    <T> Future<T> submit(Callable<T> task);
}

// 常见的线程池有以下几种，且对于所有线程池，线程都是尽量被复用
class Executors {
    // 为每一个任务创建一个线程，在线程结束时，销毁回收线程对象，且在回收时，停止创建新线程
    newCachedThreadPool()
    // 固定数量的 线程池，预先创建线程，不用为每个任务都付出创建线程的开销
    newFixedThreadPool(int number)
    // 数量为 1 的 fixed thread pool, 所有任务都是线性执行的，只有在上一个任务结束后，才会执行下一个任务
    newSingleThreadExecutor()
}

// 当希望任务能够有返回值时，Runnable 就做不到了，就需要使用新的接口 Callable
interface Callable<T> {
    T call();
}
```

## 优先级、让步
线程具有优先级，调度器倾向于让优先权最高的线程先执行。但优先级低的线程也不是不会被执行，只是执行的频率会较低。  
通常，并不会特意设置线程的优先级，只有当出现问题时，才需要去 investigate。
```java
// 通常优先级只使用  Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, Thread.NORMAL_PRIORITY 三种
// 且线程应该在 进入 run 方法时，修改当前线程的优先级
Thread.getCurrentThread().setPriority(int priority)

// 除了优先级，还可以通过主动让步，让出 CPU 的使用权
// yield 只是给调度器一个暗示，调度器并不保证该暗示会被采纳
Thread.yield()
```

## Join
线程 t1 可以调用其它线程 t2 的 join() 方法，在调用后，当前线程 t1 将会挂起，直到其它线程 t2 执行结束返回，t1 才会被唤醒，继续执行。  
在此期间，t1 是可以被 interrupt 的
```java
class Test {
    void test() {
        Thread t1;
        // 当前线程挂起，等待 t1 运行结束
        // 如果当前线程被另外的线程中断，则当前线程会抛出 InterruptedException 异常
        try {
            t1.join();
        } catch(InterruptedException e) {
            
        }

    }
}
```

## 共享资源
当多个线程访问同一资源时，就会存在资源竞争的问题，导致数据不一致，或者行为不一致。

## 原子性和易变性
原子操作是不能被线程调度机制中断的操作，即一旦开始，则必定可以在可能发生的线程切换之前完成。  在 Java 里，对除了 long/double 之外的变量的赋值和读取操作，都是原子操作。  
这是因为 JVM 可能会把 long/double 的读取、写入操作分解为两个 32bit 的操作，从而使得 可能在两个操作之间进行线程切换，继而使得不同线程对同一变量可能会看到不同的值。  
对 double/long 变量使用 volatile 修饰，可以获得简单赋值和读取操作的原子性。 
有的专家可以依靠原子性这一特点编写无锁的代码，这些代码无需加锁。但对于一般开发者，并不具备使用原子操作来替换同步的能力。尝试着移除同步，通常不会带来任何好处，反而会招致大量的麻烦。  

除了原子性之外，在如今多核系统中，可视性问题比原子性问题多的多。即一个任务做出的修改，即使是原子性的，对其它任务也可能是不可视的。  
volatile 可以解决这个问题，将一个变量声明为 volatile, 可以确保，对该变量进行写操作之后的读操作都将能看到这个改变。但是 volatile 也只能解决可视性问题，解决不了 i++ 这种操作的原子性问题。  

## 原子类
Java SE5 引入了 AtomicInteger,AtomicReference 等原子类， 来帮助实现原子操作。也提供了 Lock 等锁，帮助替代 synchronized 实现更细粒度的锁控制。  

## 线程本地存储
ThreadLocal?

## 终结线程
### 线程状态
1. New, 线程刚创建，系统分配了资源，执行了初始化。此时线程已经有资格获取 CPU 时间了，之后调度器会把这个线程转变为可运行状态或者阻塞状态
2. Ready, 就绪状态，只要获得 CPU 时间就可以执行
3. Suspended, 阻塞状态，线程因为某些原因，被组织运行。调度器会忽略处于阻塞状态的线程，不会分配给线程任何 CPU 时间，直到线程重新进入就绪状态，它才有可能运行
4. Dead, 终结状态，处于该状态的线程是不可调度的，并且再也不会得到 CPU 时间。线程终结的通常方式是从 run 方法返回。

### 进入阻塞状态
线程进入阻塞状态可能有如下原因
1. sleep 函数，线程在指定时间后被唤醒
2. 通过 wait 方法挂起，直到线程收到 notify 或者 notifyAll 消息，线程才会进入就绪状态
3. 任务在等待 IO 就绪
4. 调用 synchronized 方法，尝试获取锁失败

有的时候，我们不能等待阻塞的线程到达代码里检查其状态值的某一点，但决定让它主动的终止，那么我们就必须强制这个线程跳出阻塞的状态.  
当线程处于阻塞状态，然后执行 `t.interrupt()` 操作后，该线程就会被中断，然后抛出 InterruptedException.  
但是需要注意的是，因为 #3 和 #4 原因进入阻塞状态的线程，并不会响应中断。在写代码的时候，也可以注意到，在尝试 IO 和 获取锁的时候，并不会要求你处理 InterruptedException。  

为了中断线程，可以
1. 通过 Executor.execute(Runnable r) 来启动任务，最后通过 executor.shutdownNow() 来给所有线程调用 interrupt()
2. 通过 Executor.submit(Runnable r) 来提交任务，通过调用返回的 Future<?> res 的 cancel() 方法来中断指定线程

为了中断 #3 和 #4 的情况，对于 #3，可以通过关闭 IO 所依赖的资源，让线程退出阻塞。对于#4，可以使用 ReentrantLock 的 lockInterruptibly() 来可响应中断的获取锁 

### 检查中断
如果线程并不处于阻塞状态，可以中断吗？答案是可以的，但是代码不会抛出 InterruptedException, 仅仅只是线程的 interrupted 被置位，代码需要自己检查当前线程是否被中断
```java
public void run() {
    // 如果当前线程正在运行，但是被调用了 t.interrupt(), 那么线程的 interrupted 将被置为 true
    // t.interrupted() 会返回 true, 且在该调用中，t.interrupted 又会被复位为 false
    while(!Thread.currentThread().interrupted()) {
        // 耗时的操作
    }
}
```
如果线程处于阻塞状态被中断，则会抛出 InterruptedException 异常。在捕获异常的同时，interrupted 也会被复位。  
所以，在处理 InterruptedException 异常时，因为中断信号已经被复位，中断并不会被外界所感知。
```java
public void run() {
    while(!Thread.currentThread().isInterrupted()) {
        try {
            // 一些可能导致阻塞的调用
        } catch (InterruptedException e) {
            // 因为这里 catch 了异常，中断信号也会被复位，那么外界将不再能够感知到线程曾被中断, 前面的 while 判断也将一直返回 false
            // 为了让外界能够感知到中断行为，这里需要重新调用 interrup 来置位
            // 这也是为什么规范建议在处理 InterruptedException 时需要再调用 Thread.currentThread().interrupt()
            Thread.currentThread().interrupt()
        }
    }
}
```

## 线程之间的协作
### wait & notify
任务在执行时，有的时候需要等待其它任务的执行结果。wait 提供了让当前线程失去对锁的持有并且挂起的能力，只有当接收到 notify 或者 notifyAll 的通知后才会重新激活。  
且 t.notify 和 t.notifyAll 也只会激活当前正在等待 t 的线程。  
所以 wait 必须在同步方法或者同步代码块里调用，因为 wait 会导致当前线程失去锁的持有。如果在非同步代码里调用，虽然编译能通过，但是执行时会抛出异常。notify 和 notifyAll 也类似，都必须在同步代码里被调用。  
需要注意的是，线程在接收到 notifyAll 之后，会被激活，这个时候，所有被激活的线程都需要重新去竞争锁，竞争成功的线程才可以继续执行。  
通常，线程都是因为某个条件没达到，才需要 wait, 等待外界变化。在线程被 notify 激活后，就会去尝试竞争锁。当最后获取锁时，需要再次去检查条件，因为这个时候，条件很可能已经被其它在线程之前获取锁的线程所改变。
```java
class Consumer {
    public void consume() {
        // 假设有多个 consumer 在消费同一个 items
        synchronized (lock) {
            // items 是一个被多线程所共享的一个数组
            // 如果这里 if 来检查，那么当线程被唤醒且获取锁时，items 可能已经被其它线程锁消费了，这个时候 items 仍然是 空的，但是因为 这里使用的是 if, 那么在唤醒后，将直接退出 if 判断，执行 items.pop()
            // 这样就会产生错误。所以为了避免这种问题，这里的检查就应该是 while(items.isEmpty())
            // 那么在线程重新获取锁后，也会再次检查条件，如果条件不满足，将继续 wait
            if (items.isEmpty()) {
                // 当consumer 发现当前没有 items 时，就会挂起等待外界条件变化
                lock.wait();
            }
            // 当某一个 consumer 被 notifyAll 唤醒且成功拿到锁后，就会消费掉该 item
            // 当消费结束后，也会释放掉 lock, 因为退出了同步代码块
            items.pop()
        }
    }
}

class Generator {
    // generator 可以产生 item
    public void generate() {
        items.push(item)
        // 当产生了一个 item 后，通知所有 consumer
        lock.notifyAll()
    }
}
```
### Lock & Condition
手动调用 wait 和 notify 显得比较晦涩， Java SE5 提供了工具类 `Condition`来帮助实现这类功能。
```java
public class Car {
    private Lock lock = new ReetrantLock();
    private Condition condition = lock.newCondition();
}
```

### BlockingQueue
使用 notify 或者 wait 是很低级的通信方式，可以直接使用并发工具包提供的多种并发集合，比如常用的 `LinkedBlockingQueue`
```java
// 队列有可能有容量限制，当在容量已满的情况下，尝试插入新的元素，则会失败
public interface BlockingQueue<E> extends Queue<E> {
    // 立即插入队列，如果成功，则返回 true, 如果失败，则抛出异常
    boolean add(E e);

    // 立即尝试插入队列，如果成功，返回 true, 如果失败，返回 false, 不抛异常
    boolean offer(E e);
    // 等待指定时间插入元素
    boolean offer(E e, long timeout, TimeUnit unit);

    // 尝试插入队列，如果不成功，则挂起等待
    void put(E e) throws InterruptedException;

    // 弹出 队列的 head 元素，如果没有元素，则挂起等待
    E take() throws InterruptedException;
    // 在挂起之前，尝试等待指定时间
    E poll(long timeout, TimeUnit unit) throws InterruptedException;

    // 如果队列元素满足 o.equals(element)， 则删除该元素。如果队列有改变，则返回 true
    boolean remove(Object o)

    boolean contain(Object o)
}
```
使用 `BlockingQueue` 可以避免显式的使用同步，其同步是由 队列隐式的完成的

## 死锁
死锁发生的必备条件
1. 资源是互斥的，不可被共享，即如果一个资源被一个线程占用，其它线程则必须等待资源被释放。 且不可抢占资源，不可强迫占有资源的线程立即释放资源
2. 存在循环等待。即 A 等待 B, B 等待 C, C 又等待 A. 至少存在一个线程，在占用某资源的情况下，仍然尝试等待被其它线程占用的资源。

## 并发工具类
```java
// CountDownLatch 一般被用作，将一个大的 task 拆分为几个相互独立的小 task, 其它等待该 task 完成的线程在 CountDownLatch 计数为 0 之前会被挂起，在计数为0时被唤醒开始执行
public class CountDownLatch {

    public CountDownLatch(int count) {

    }

    // 计数减 1
    public void countDown() {

    }

    // 等待该 CountDownLatch 计数为 0
    public void await() throws InterruptedException;

    // 返回 当前 count
    public long getCount();
}
```

```java
// Future
public interface Future<V> {

    // 中断执行该 task. 如果 task 已经 finish, 则没有效果，如果 task 正在执行，则根据 interruptedIfRunning 判断是否中断线程。如果 task 还没有开始，则该 task 不能执行 run
    // 如果该 task 不能被 cancel, 则返回 false, 反之 返回 true. 但该返回值并不表示 task 已经被 cancel, 需要 check isCanceled()
    boolean cancel(boolean interruptedIfRunning);

    // 如果该 task 在结束之前被取消，则返回 true
    boolean isCanceled();

    // 检查 task 是否结束，包括 正常运行结束，抛出异常结束，被中断结束。换成 isCompleted 更恰当一些
    boolean isDone();

    // 尝试获取 task 结果，如果还没有结束，则等待，如果被中断，则抛出 InterruptedException，如果运行过程中出错，则抛出 ExecutionException
    V get() throws InterruptedException, ExecutionException;

    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

```java
// Future 本身也是一个 Runnable, 在 Runnable 结束之时，表示 Future complete, 然后返回结果
public interface RunnableFuture<V> extends Runnable, Future<V> {
    /**
     * Sets this Future to the result of its computation
     * unless it has been cancelled.
     */
    void run();
}

public class FutureTask<V> implements RunnableFuture<V> {

    // 当前 FutureTask 的状态
    // 初始创建的时候是 New, 在 任务运行中，也是 New
    // 在任务结束，如果是正常结束，就首先尝试将状态改为 COMPLETING，如果成功，最后改为 Normal, 表示正常结束
    // 如果是异常结束，也是首先尝试将 状态改为 COMPLETING，如果成功，则把状态改为 EXCEPTIONAL
    // 这里的 COMPLETING 就是用作当多个线程执行同一个 FutureTask 时

    // 当 task 被取消，如果当前状态不是 New, 或者不能改成 INTERRUPTING 或者 CANCELLED， 则表示状态已经被更改，不能取消
    // 如果 interruptedIfRunning 为 true, 则 尝试将状态改为 INTERRUPTING，并接着调用 线程的 interrupt() 来中断执行线程, 最后把状态改为 INTERRUPTED。如果为 false, 则改为 CANCELED. 

    // 所以 task 可能的 状态变化列举如下
    // NEW -> COMPLETING -> NORMAL, 表示正常结束
    // NEW -> COMPLETING -> EXCEPTIONAL，表示异常结束
    // NEW -> CANCELLED
    // NEW -> INTERRUPTING -> INTERRUPTED
    private volatile int state;
    private static final int NEW          = 0;
    private static final int COMPLETING   = 1;
    private static final int NORMAL       = 2;
    private static final int EXCEPTIONAL  = 3;
    private static final int CANCELLED    = 4;
    private static final int INTERRUPTING = 5;
    private static final int INTERRUPTED  = 6;

    // 当状态不为 NEW, 则表示已经 completed
    public boolean isDone();
    // 当状态 >= 4, 则表示被取消了
    public boolean isCanceled();

    public FutureTask(Callable<V> callable);

    public FutureTask(Runnable runable, V res);
}
```
