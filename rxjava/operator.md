# Operator
Publisher 是以流的形式来提供事件，在消费者进行消费之前，可能有许多其它的操作，比如常见的 filter.  
rxjava 为我们提供了非常全面的操作符，大致分为以下几类
1. 变换，如 map, scan
2. 过滤，如 filter, debounce, distinct
3. 聚合，如 switch, reduce
4. 调度，如 subscribeOn, observeOn


# 创建操作
创建一个 observable
## create
使用 create 方法，传入一个 OnSubscribe 的实现
```java
Observable.create(new Observable.OnSubscribe<Integer>() {
    @Override
    public void call(Subscriber<? super Integer> observer) {
        try {
            if (!observer.isUnsubscribed()) {
                for (int i = 1; i < 5; i++) {
                    observer.onNext(i);
                }
                observer.onCompleted();
            }
        } catch (Exception e) {
            observer.onError(e);
        }
    }
 } ).subscribe(new Subscriber<Integer>() {
        @Override
        public void onNext(Integer item) {
            System.out.println("Next: " + item);
        }

        @Override
        public void onError(Throwable error) {
            System.err.println("Error: " + error.getMessage());
        }

        @Override
        public void onCompleted() {
            System.out.println("Sequence complete.");
        }
    });
```
## defer
直到有观察者订阅，才创建 observable, 并且为每个观察者都创建一个新的 observable
```java
// 该方法也会返回一个 observable, 只是这个 observable 用来响应当订阅产生时，调用 observable factory 产生 observable 供观察者订阅
Observable.defer(() -> {
    // observable factory, 用于在观察者订阅时，产生新的 observable
}).subsribe(Subscriber s)// 当订阅产生时，该 observable 会通过 observable factory 产生 新的 observable o，然后调用 o.subscribe(s)
```
## Empty/Never/Throw
有的时候，我们需要一些特殊的 observable, 比如测试的时候.  
* Empty 表示创建一个不发射任何数据，但是正常终止的 observable
* Never 表示创建一个不发射任何数据，也不终止的 observable
* Throw 表示创建一个不发射数据，以一个错误终止的 observable

```java
Observable.empty()

Observable.never()

Observable.error(Throwable e)
```
## From
支持将其它种类的对象和数据类型转换为 observable.  比如 Iterable 可以看作同步的 observable, Future 可以看作 Single.  
在 rxjava 里，from 操作符可以将 数组/Future/Iterable/Callable 转换为 Observable
```java
Observable.from(array)

Observable.from(iterable)

Observable.from(future)

Observable.from(callable) // callable 将返回一个对象
```

## Interval
创建一个按固定时间间隔发射整数序列的 observable
```java
Observable.interval(long period, TimeUnit unit)
```

## Just
创建一个发射指定值的 Observable. 和 from 类似，但是 Just 会把传入值当作一个整体，原样发射，但是 from 会将传入的 数组或者 iterable 的数据依次发射。
```java
// 也可以传入多个参数，按顺序将参数依次发射
Observable.just(T item)
Observable.just(T item1, T item2, T item3)
```
## Range
创建一个发射特定整数序列的 Observable
```java
// start 是起始值，count 是个数
Observable.range(int start, int count)
```
## Repeat
创建一个发射特定数据重复多次的 observable, 有的实现允许你重复的发射某个数据序列，还有一些允许你限制重复的次数。  
repeat 返回的 observable 默认在 trampoline 调度器上执行
```java
// 以当前 observable 作为 source 传入repeat observable, 该 repeat observable 则会重复发射 当前 observable 里的数据
Observable repeat(long times) {
    return new ObservableRepeat<T>(this, times)
}

public final class ObservableRepeat<T> extends AbstractObservableWithUpstream<T, T> {
    final long count;
    // 调用者作为真正的 source 传入， count 为需要重复的次数
    public ObservableRepeat(Observable<T> source, long count) {
        super(source);
        this.count = count;
    }

    @Override
    public void subscribeActual(Observer<? super T> s) {
        SequentialDisposable sd = new SequentialDisposable();
        s.onSubscribe(sd);
        // 当该 repeat observable 被订阅者时，s 作为真正的观察者传入
        RepeatObserver<T> rs = new RepeatObserver<T>(s, count != Long.MAX_VALUE ? count - 1 : Long.MAX_VALUE, sd, source);
        // 开始新的一轮订阅
        rs.subscribeNext();
    }

    static final class RepeatObserver<T> extends AtomicInteger implements Observer<T> {

        private static final long serialVersionUID = -7098360935104053232L;

        final Observer<? super T> actual;
        final SequentialDisposable sd;
        final ObservableSource<? extends T> source;
        long remaining;
        RepeatObserver(Observer<? super T> actual, long count, SequentialDisposable sd, ObservableSource<? extends T> source) {
            this.actual = actual;
            this.sd = sd;
            this.source = source;
            this.remaining = count;
        }

        @Override
        public void onSubscribe(Disposable s) {
            sd.replace(s);
        }

        @Override
        public void onNext(T t) {
            actual.onNext(t);
        }
        @Override
        public void onError(Throwable t) {
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            // 当 source 发射完数据，它会调用 repeat observer 的 onComplete
            // repeat observer 会检查 remain 次数，如果不为 0， 则自己再重新订阅 source, 开始新的一轮 
            long r = remaining;
            if (r != Long.MAX_VALUE) {
                remaining = r - 1;
            }
            if (r != 0L) {
                subscribeNext();
            } else {
                // 当重复次数为 0 时，订阅关系真正的结束
                actual.onComplete();
            }
        }

        /**
         * Subscribes to the source again via trampolining.
         */
        void subscribeNext() {
            // 当开始订阅时
            if (getAndIncrement() == 0) {
                int missed = 1;
                for (;;) {
                    if (sd.isDisposed()) {
                        return;
                    }
                    // 传入的 source 和观察者真正发生订阅关系
                    // 当 source 开始发射数据，repeat observer 会作为中介，调用真正的 观察者的回调
                    source.subscribe(this);

                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        break;
                    }
                }
            }
        }
    }
}
```
## start
接受 function, callable, future, runnable 等作为参数，调用这个函数获取一个值， 然后返回一个发射这个值给后续观察者的 observable.  
即使有多个订阅者，该函数只会被执行一次。

## Timer
创建一个 observable, 它在一个给定的延迟后发射一个特殊的值。
```java
public final class ObservableTimer extends Observable<Long> {
    final Scheduler scheduler;
    final long delay;
    final TimeUnit unit;
    public ObservableTimer(long delay, TimeUnit unit, Scheduler scheduler) {
        this.delay = delay;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    @Override
    public void subscribeActual(Observer<? super Long> s) {
        TimerObserver ios = new TimerObserver(s);
        s.onSubscribe(ios);

        // ios 是一个 observer, 也是一个 runnable
        // 该 runnable 被 scheduler 调度，在指定延迟后执行
        Disposable d = scheduler.scheduleDirect(ios, delay, unit);

        ios.setResource(d);
    }

    static final class TimerObserver extends AtomicReference<Disposable>
    implements Disposable, Runnable {

        private static final long serialVersionUID = -2809475196591179431L;

        final Observer<? super Long> actual;

        TimerObserver(Observer<? super Long> actual) {
            this.actual = actual;
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
        }

        @Override
        public boolean isDisposed() {
            return get() == DisposableHelper.DISPOSED;
        }

        @Override
        public void run() {
            if (!isDisposed()) {
                // 在执行的时候，发射一个 特殊值 0
                actual.onNext(0L);
                lazySet(EmptyDisposable.INSTANCE);
                // 然后调用订阅者的 onComplete
                actual.onComplete();
            }
        }

        public void setResource(Disposable d) {
            DisposableHelper.trySet(this, d);
        }
    }
}
```