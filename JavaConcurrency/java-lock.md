# Java Lock
## Lock
为了更好的支持 并发，JDK 1.5 发布了 Lock 接口
```java
interface Lock {
    void lock()//尝试获取锁，获取后，该方法返回
    void lockInterruptibly() throws InterrupedException//与 lock 的区别在于该方法可以在获取锁的过程中响应中断
    boolean tryLock()//尝试非阻塞的获取锁，调用后，立即返回。如果成功，返回 true, 如果失败，返回 false
    boolean tryLock(long time, TimeUnit unit) throws InterrupedException//当在时限内获取锁，则返回 true, 当在时限内被中断，响应异常处理，当超时仍未获取锁，则返回 false
    void unlock()//释放锁
    Condition newCondition()//创建一个和当前 lock 对象绑定的 condition 对象
}
```
简单来说，*Lock* 用来增强 *synchronized*, 提供一些额外的特性， *Condition* 用来增强对锁对象的 *wait*, *notify* 等方法的使用。

## 队列同步器
队列同步器 *AbstractQueuedSynchronizer* 是用来构建锁的基础框架，使用一个 int 变量表示同步状态。 它提供了一些基础方法，可以供锁的实现者调用来实现自己的锁的逻辑。  
它提供的方法主要分为三类：
* 独占式的获取和释放同步状态
* 共享式的获取和释放同步状态
* 查询同步队列中的等待线程

总体来说，同步器是一个桥梁，连接线程访问以及同步状态控制等底层技术和不同锁(比如 Lock, CountDownLatch)的接口定义.

## 重入锁
重入锁就是支持重入的锁，它表示已经获取了锁的线程，还可以继续获取锁而不会被阻塞。*synchronized* 就是一个隐式的重入锁。  
重入锁在多次获取锁时，需要进行计数自增。在释放锁的时候，需要计数自减。当计数为 0, 表示锁被释放，其它线程可以尝试获取该锁了。  
重入锁有公平和非公平之分，公平的重入锁是在指先等待锁的线程先尝试争夺锁，非公平的重入锁是指线程在可以尝试获取锁的时候，立即尝试，而不管同步队列里是否已经有了其它等待更久的线程。 
因为一般情况下，释放锁的线程接下来继续尝试获取锁的概率很大，所以导致公平锁会有更多的线程切换，降低了效率，而非公平锁则保证了吞吐量，却有可能导致其它线程等待太久而饥饿。  

## 读写锁
前面讲到的锁都是排它锁，同一时刻只能有一个线程持有锁，任何其它线程想获取锁，则必须阻塞等待。  
读写锁允许同一时间有多个读线程访问，当写线程访问时(此时不能有读线程或者写线程, 否则不能保证写线程的结果对其它线程的可见性)，阻塞后续所有读线程和写线程, 且写线程的操作对其它读线程和写线程可见。这样通过读锁和写锁分离，使得在一般场景下(读操作多于写操作), 并发性比一般的排他锁有了很大提升。  
读写锁的意义：
* 如果只有读操作，那么不需要加任何锁
* 如果不需要保证写线程的更新立即对其它线程可见，那么也可以不加锁
* 如果需要保证总是读取最新数据，那么就需要加锁，因为要保证写操作的结果对读线程可见
* 如果使用一般的排他锁，那么不管是读还是写，都只有一个线程可以获取锁，但是读写锁允许同时存在多个读线程，虽然对于写操作，和排他锁的效果一样，但是在读明显多于写的场景下，读写锁可以大大增加性能  
* 读写锁可以锁降级，即线程在持有写锁后，继续持有读锁。这样是为了保证，线程更改了数据后使用该数据时，该数据不会被其它线程再次修改了。比如，如果写线程在更新操作之后，直接释放了写锁，它之后还需要读取数据，但是其它线程就在这个时候获取了写锁，然后修改了数据。回到之前那个写线程，因为没有读锁，该线程没法感知到数据已经又被修改过了，它会使用它之前自己修改后的值。
* 读写锁不可以升级，即线程在持有读锁后，不可以继续持有写锁。因为可能有多个线程都持有了读锁，如果一个线程可以先持有读锁，然后持有写锁，那么它写操作的结果就没法保证对其它持有读锁的线程可见，这和读写锁的定义违背。

## Condition
常规的 *synchronized* 锁具有 *wait*, *notify* 机制，即持有锁的线程，可以通过 *lockObject.wait* 方法释放自己对锁的持有，然后将自己状态改为 WAITNG, 并且不再受 CPU　调度，只能等待 *lockObject.notify* 通知，从而有机会重新进入等候队列，继续争夺锁。如果争夺锁成功，该线程将从 *lockObject.wait* 处返回并继续执行。  
该方式的特点在于，想要调用 *wait*, *notify* 方法，该线程必须已经获取了 *lockObject*, 即这些方法只能在 *synchronized* 代码块里调用，并且这些方法只能通过 *lockObject* 来调用。  
其次，因为 *lockObject* 只有一个，所以它也只能维护一个等候队列， 它也不支持线程在等候时仍然响应中断。  
而在新的 *Lock* 框架下，该怎么实现类似的效果呢？就是通过 *lock.newCondition()* 来实现。  
*lock.newCondition* 返回的 condition object 作用和 lockObject 类似，可以在获取锁之后，通过 *conditionObject.await* 方法释放锁并等待通知，不同在于 *lock* 可以有多个不同的 condition object，即可以有等待在不同条件上的多个等候队列。  
与 *lockObject.notify* 对应的是 *conditionObject.signal* 方法。
