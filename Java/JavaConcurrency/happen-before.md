# happen-before
Java 内存模型规定，如果一个操作的结果要对另一个操作可见，那么这两个操作之间必须存在 happen-before 关系。这两个操作可以在同一线程，也可以在不同的线程。  
1. 程序顺序规则：一个线程中的每个操作，happen before 于该线程中的任意后续操作
2. 监视器规则：锁的解锁操作，一定 happen before 的随后对该锁的加锁
3. volatile: 对该变量的写操作，一定happen-before 于后续对该变量的读操作
4. 传递性：如果A 操作 happen before B, B happen before C, 则 A happen before C
5. start 规则：如果线程 A 执行 线程 B.start() 操作，那么 A 线程 B.start() 操作happen before 与 B 线程中的任意操作
6. join 规则： 如果线程 A 执行线程 B.join() 操作并成功返回，那么线程 B 中的任意操作 happen before 于线程 A 从 线程 B.join() 操作成功返回。

注意， A happen before B, 不代表 A 一定在 B 之前执行，只意味着A 操作的结果对 B 操作可见， 且A 按顺序排在 B 之前。但是 JMM 规定，只要重排序之后的结果，和按照 happen before 的顺序来执行是一样的，那么这样的重排序就是允许的。
```java
double pi = 3.14;//A
double r = 3;//B
double area = pi * r * r;//C
```
按照程序顺序规则，A happen before B, B happen before C, A happen before C. 但是 B 和 A 之间并没有依赖关系，B 有可能在 A 之前执行。  

## volatile
volatile 内存语义的实现，其实可以看作是对单个变量的读写操作使用同一个锁。而锁的语义保证了原子性，以及解锁总是 happen before 加锁。所以，只要任意线程对 volatile 变量进行了写操作，那么后续任意线程对该变量的读，都会读到最新值。  
volatile 的内存语义结合上程序顺序规则，可以实现线程之间的通信。
```java
public class Demo {
    int a = 0;
    volatile boolean flag = false;

    public void write() {
        a = 2;//1
        flag = true;//2
    }

    public void read() {
        if (flag) {//3
            int i = a;//4
        }
    }
}
```
volatile 自己本身的语义保证了对该变量的写，一定对后续对该变量的读可见。所以当线程 A 执行 write 方法，线程 B 执行 read 方式，且线程B 检测到 flag 为 true 时， 表示 A 一定已经执行完了 write 方法。根据程序顺序规则，1 happen before 2, 3 happen before 4, 又 2 happen before 3, 那么 1 一定 happen before 4, 所以在执行 4 的时候，a 一定已经为更新之后的值。这就保证了A　线程在写 volatile 变量之前所有可见的共享变量，在 B 线程读同一个 volatile 变量之后，都立即变的对 B 线程可见。  

volatile 本身的特性就是相当于对一个变量的读写加锁，根据锁的 happen before 原则，前面对该变量的写总是对后续对该变量的读可见。  
同样，锁的语义也决定了临界区代码的原子性，这就意味着，即使是对 long/double 类型的变量读写，只要它是 volatie, 对该变量的读写就具备原子性。  

volatile 的用处不在于它本身的特性，在于它对内存可见性的影响，通过 volatile 实现线程之间的通信，如上例所示。  
当一个 read 线程检查到 flag 为 true 时, 那么根据 happen before 原则， write 线程对共享变量 a 的修改就一定对 read 线程可见

## 锁
类似的，锁除了可以让临界区互斥执行外，还可以让释放锁的线程向获取同一个锁的线程发送消息。  
锁和 volatile 具备类似的内存语义，在一个线程释放锁后，该线程对共享变量的改变，一定会对之后获取该锁的线程可见。  
```java
class Demo {
    int a = 0;

    public synchronized void generate() {//1, 获取锁
        a++//2, 执行同步代码块
    }//3, 释放锁

    public synchronized void consume() {//4, 获取锁
        int i = a;//5, 执行同步代码块
        //....
    }//6， 释放锁
}
```
假设线程A先执行 generate, 线程B再执行 consume, 那么根据程序顺序规则，1 before 2, 2 before 3, 4 before 5, 5 before 6. 又因为 3 befoer 4, 所以 2 before 5.

## final
JMM 对 final 变量作了三个重排序规则
1. 在构造函数里对 final 变量的写入，与随后把这个被构造对象的引用赋值给一个引用变量，这两个操作之间不能重排序. 即 final 域的写操作不能重排序到构造函数之外。
2. 初次读一个包含 final 域的对象的引用，与随后初次读这个 final 域，这两个操作之间不能重排序。即保证在读取 final 域之前，一定先读取包含该 final 域的对象的引用。
3. 如果 final 域是引用类型，在构造器内对 final 引用的对象的成员域的写入，与随后在构造器之外把这个被构造对象赋值给一个引用变量，这两个操作不能重排序。

```java
public class Demo {
    int i;
    final int j;
    static Demo demo;

    public Demo() {
        i = 1;
        j = 2;
    }

    public static void write() {
        demo = new Demo()
    }

    public static void read() {
        Demo d = demo;
        if (d != null) {
            int x = d.i;
            int y = d.j
        }
    }
}
```
总而言之，这些重排序规则就确保了，在引用变量为任意线程可见之前，该引用变量执行的对象的 final 域已经在构造函数中被正确初始化过了。但是普通变量就不具备这种性质。  
比如上例中的普通变量 i, 有可能它的初始化就被重排序在了构造器之外，导致 read 函数里 d.i 会返回错误的值。  

但为了保证重排序规则的效果，还需要保证，在构造器内部，不能让被构造对象的引用逸出，从而可能在对象构造器还没有完成的时候，就被其它线程所见。
```java
public class Demo {
    final int i;
    static Demo obj;
    public Demo() {
        i = 1;
        obj = this;
    }
}
```
以上代码就有可能导致在一个线程执行构造器的时候，还没有结束，其它线程就可以检测到 obj 已经不为 null, 从而访问 obj 的数据，但这个时候 final 域可能还没有初始化，因为 i= 1 与 obj=this 可能被重排序。

## 双重检查锁定和延迟初始化

```java
public class Test {
    private static Test instance;

    // 这个是最简单的实现，但是有明显的错误，即在多线程条件下，很有可能一个线程执行到 #1 的时候，另一个线程执行到 #2.
    // 导致在不同的线程都创建了实例
    public static Test getInstance() {
        if (instance == null) { //1
            instance = new Test()//2
        }
        return instance;
    }

    // 一个简单的 fix 就是加上 锁，但是如果 getInstance 被频繁调用，那么就会导致较大性能损耗
    public synchronized static Test getInstance() {
        if (instance == null) { //1
            instance = new Test()//2
        }
        return instance;
    }

    // double check
    public static Test getInstance() {
        if (instance == null) {//1 实例在初始化完成之后，所有的 get 都不再需要加锁
            synchronized(Test.class) {
                if (instance == null) { //2 这里必须再检查一次，否则有可能多个线程都通过了 #1
                    instance = new Test()
                }
            }
        }
        return instance;
    }

    // 上述的 double check 还存在一定的风险，这是因为 instance = new Test() 这条语句其实对应多条操作，这些操作可能会被重排序
    // 比如 JVM 可能在分配内存后，先把引用赋给  instance, 然后再执行 instance 的初始化。 这就导致有可能另外的线程在执行 #1 时，发现 instance 不是 null, 然后去使用它，但是实际上该对象还没有执行初始化代码
    // 为了解决这个问题，可以将 instance 标记为 volatile. volatile 会禁止 instance = new Test() 的重排序
    // 当然，更好的方式是使用 内部 holder 的形式

    static final class TestHolder {
        public static Test INSTANCE = new Test()
    }
    // JVM 保证类的初始化过程的同步
    public static Test getInstance() {
        return TestHolder.INSTANCE;
    }

}
```