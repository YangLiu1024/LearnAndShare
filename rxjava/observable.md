# Observable
observable 是 publisher 在 rxjava 里的实现
## Single
Single 是 observable 的一个变种，它总是只发射一个而不是一系列 事件。 因此，订阅 Single 只需要 onSuccess 和 onError 两个响应即可. 且 Single 只会调用这两个方法中的一个，调用之后，订阅关系结束。 