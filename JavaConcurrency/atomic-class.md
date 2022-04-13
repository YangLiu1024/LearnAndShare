# 原子操作类
## unsafe
unsafe 提供了一些 native api 来保证操作的原子性。
```java
public final native boolean compareAndSwapObject(Object o, long offset, Object expect, Object x);

public final native boolean compareAndSwapInt(Object o, long offset, int expect, int x);

public final native boolean compareAndSwapLong(Object o, long offset, long expect, long x);
```

## 原子更新基础类型
Java 提供了一系列的原子操作类，比如 AtomicBoolean, AtomicInteger, AtomicLong 等等。它们提供的 API 基本相同. 以 Integer 为例
```java
public final int getAndIncrement() {
    for(;;) {//如果 CAS 失败，则循环继续尝试
        int current = get();
        int next = current + 1;
        if (compareAndSet(current, next)) {//CAS 是通过 unsafe 来实现的
            return current;
        }
    }
}
```
## 原子更新数组元素
原子更新数组元素和原子更新基础类型差不多，只是多了索引的参数。
## 原子更新引用类型
假设我们的使用场景是拿到当前引用，进行一定操作，然后更新引用。这种操作在多线程情况下，是会有问题的。
```java
public class AtomicReferenceTest {

    private static volatile BankCard bankCard = new BankCard("yang",100);

    public static void main(String[] args) {

        for(int i = 0;i < 10;i++){
            new Thread(() -> {
                // 先读取全局的引用
                final BankCard card = bankCard;
                // 构造一个新的账户，存入一定数量的钱
                BankCard newCard = new BankCard(card.getAccountName(),card.getMoney() + 100);
                System.out.println(newCard);
                //即使 bankCard 是 volatile 变量，仍然会有多线程问题，因为即使读取操作和赋值操作都是原子操作，但是结合起来就不是了
                //比如线程1 读取了引用，挂起，然后线程2 也读取了引用，最后，两个线程的引用赋值操作，肯定会被覆盖掉
                bankCard = newCard;                // 最后把新的账户的引用赋给原账户
                try {
                    TimeUnit.MICROSECONDS.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```
为了解决引用赋值问题，引入 AtomicReference, 这样就保证了在更新引用时，没有其它线程已经提前更新了引用。
```java
public class AtomicReferenceTest {

    private static AtomicReference<BankCard> bankCardRef = new AtomicReference<>(new BankCard("yang",100));

    public static void main(String[] args) {

        for(int i = 0;i < 10;i++){
            new Thread(() -> {
                while (true){
                    // 使用 AtomicReference.get 获取
                    final BankCard card = bankCardRef.get();
                    BankCard newCard = new BankCard(card.getAccountName(), card.getMoney() + 100);
                    // 使用 CAS 乐观锁进行非阻塞更新
                    if(bankCardRef.compareAndSet(card,newCard)){
                        System.out.println(newCard);
                        break;
                    }
                }
            }).start();
        }
    }
}
```