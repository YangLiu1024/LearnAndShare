rxjava 只有一个依赖，reactive-stream, 它是一项响应式编程 API 标准。  
reactive-stream 定义了 3 个接口共计 7个方法， 这 7 个方法构成了 整个 reactive 世界。  

```java
// 事件提供者，提供接口，让外部可以监听自己. 同时也可以持有订阅者的引用，在有事件产生时，调用订阅者的 对应 handler
interface Publisher<T> {
    void subscribe(Subsriber<? extends T> subscriber)
}

// 事件消费者，需要提供 在事件产生，事件结束，产生错误时的回调
interface Subscriber<T> {
    void onNext(T t);
    void onCompleted();
    void onError(Throwable t);

    // 在消费者向提供者发出订阅请求后，提供者需要调用消费者的 onSubscribe(Subscription s) 方法，把产生的订单凭证(Subscription)交给订阅者
    // 订阅者就可以通过 该订单凭证，请求事件，或者取消订阅
    void onSubscribe(Subscription s);
}

// 是 事件提供者，和事件消费者之间通信的媒介，订阅者可以通过该媒介向提供者请求事件，或者告知取消订阅
interface Subscription {
    void request(long n);
    void cancel()
}
```
举个例子说明，比如你是 订阅者，你想订阅某杂志今年每个月的刊物，你实现了 Subscriber 的接口，定义了当收到一份刊物，当杂志社发出今年所有刊物，或者杂志社倒闭这些事件的处理方式。  
这个时候。杂志社作为 Publisher, 可以使用 *subscribe* 方法，接收到你的订阅。 在收到你的订阅后，它会调用订阅者的 *onSubscribe* 方法，告诉订阅者，订单以成立，且把订单 subscription 交给订阅者。  
订阅者拿到 subscription 后，就可以通过该订单，请求杂志社 寄出刊物，或者取消订阅。