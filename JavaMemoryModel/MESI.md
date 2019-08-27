Introduction to MESI

# Definition
The MESI protocol is an invalidate-based cache coherence protocol, and is one of the most common protocol which support write-back caches.
1. Modified(M): only valid in current cache, and is dirty, other cache can not access its responding cache line
2. Exclusive(E): only valid in current cache, but is clean
3. Shared(S): be stored in other caches, and is clean
4. Invalid(I): invalid
![](image/mesi-status.png)

对于M状态， 因为 MESI 支持 write-back, 这个dirty 的 cache entry 会在之后的某个时间点写回主存。如果在此期间， 有其它的 core 要访问同一 cache entry 地址， 当前 core 会接收到这一事件，并且将dirty 的cache entry 写回到主存，并且将该 cache line 标记为 shared.
对于E状态， 如果其它 core 要读这个 cache line, 则将状态改为 share. 如果当前core 要写该 cache line, 则将状态改为 M. E的作用在于，如果CPU 想给 S状态的cache line 写，那么必须发出 bus 信息让其它 cache invalidate. 但是如果给E状态的写，则不需要bus transaction.
对于S状态，如果其它 core 修改了它的 cache line, 则当前 core 的该cache line 会被标记为 Invalid
对于I状态，表示当前cache entry invalid 且 unused, 可以被优先 replace

## Events
cache entry 的状态可以根据不同的事件进行更新。

Event Name  | Comment
------------| -------
PrRd | processor request to read a cache entry
PrWr | processor request to write a cache entry
BusRd | another processor request to read a cache entry
BusRdX | another processor which does not have the entry in the cache request to write the cache entry
BusUpgr | another processor which have the entry in cache request to write the cache entry
Flush | an entire cache entry is written back to main memory by another processor
Flushopt | an entire block is posted on the bus in order to supply it to another processor(cache to cache transfer)


对于bus event, 有的书本也有另外一套称呼

Event Name | Comment
-----------| -------
Read | another processor request to read a cache entry(same to BusRd), the message contain the physical address of the cache line
Read Response | the message contain the data requested by an earlier Read message, its supplied by either main memory or another cache
Invalidate | the message contain the physical address of cache line to be invalidated, all other caches need to invalidate there copies
Invalidate Acknownledge |  A CPU recieve Invalidate message must repsond with Invalidate Acknownledge message after invalidate their copies
Read Invalidate | the combination of Read and Invalidate
Writeback | the message contain both the data and its physical address to be written back to memory or another CPU cache

M 和 E 的状态总是精确的， 但是S不是。 当其它 S cache entry 被它们的cache discard时，它们并不会通知当前 cache, 则当前的cache entry事实上是 Exclusive，但是它的状态仍然是S。

## Handle PrRd/PrWr
处于不同状态的 cache entry 在处理自己处理器 event 时的 response 如下表所示
![](image/mesi-change.png)

## Handle bus events
处于不同状态的 cache entry 在处理其它处理器发出的 event 时的response 如下表所示
![](image/mesi-recieve.png)

## Example
An example to show how MESI works
![](image/mesi-example.png)

1. 首先 P1 发出 PrRd, 发现cache 中没有，则给 bus 发出 BusRd. 因为其它 cache 都没有该 cache line, 则将状态改为 E, 并且通过 memory controller 从主存中读取数据。
2. P1 发出 PrWr, 将当前 cache entry 状态改为 M
3. P3 发出 PrRd, 发现自己没有该 cache entry, 则发出 BusRd, P1 接收到该信息后，将自己的 cache entry 标记为 S,且通过 memory controller 将 cache entry 同步到主存，并且将cache entry 发给 P3
4. P3 发出 PrWr, 将状态改为 M, 并且发出 BusUpgr. P1 接收到后，将自己的 cache entry 标记为 I
5. P1 发出 PrRd, 发现是 Invalid, 则发出 BusRd, P3 接收到后，将自己的 cache entry 标记为 S, 通过 memory controller 写回内存，并且通过 bus 发给 P1
6. P3 发出 PrRd, 发现自己 cache entry 是 S, 则直接读取返回
7. P2 发出 PrRd, 然后发出 BusRd, P1 或者 P3 接收到这个事件，将自己的 cache entry 发给 P2

# Disadvantages of MESI
MESI protocol 也有自己的缺点， 
1. 当 write/read invalid cache entry 时，可能需要等待很久，因为要stall 直到收到所有其它 CPU 的 invalidation acknownledge.这是发送端的阻塞。
2. 当需要 invalidate cache entry in own cache, 也可能需要花费很长时间。这是接收端的阻塞。

## Store buffer
为了解决问题 #1， 引入 store buffer
![](image/cache-store-buffer.png)

如果当前 core 并不own 想写入的 cache line, 即该 cache line 不在 当前core cache 里面，或者状态不为 E 或者 M, 为了写入值， 当前 core 需要发出 read-invalidate 信号来 invalidate 其它 core, 这时需要stall 来等待其它 core 的 ACK. 这样会消耗很多时间。为了避免这种情况，引入了 store buffer. 当前core 可以先把 write 写入 store buffer, 发出 read-invalidate 信号后继续执行之后的命令，无需等待该信号返回。这样就可以节省很多 CPU 之间通信的时间。然后异步等待其它 CPU 的响应， 当响应时，将store buffer apply to cache.
但是这种策略也引入了另一个问题: 由内存系统引起的重排序。 参考以下代码示例：
```java
void foo(void) {
  a = 1;
  b = 1;
}

void bar(void) {
  while (b == 0) continue;
  assert (a == 1);
}
```
<details>
  <summary>A possibility of sequence of operations</summary>
  
  假设 CPU0 执行 foo(), CPU1 执行 bar(). 且变量 a 只在 CPU1 cache 里，变量 b 只在 CPU0 cache 里。
  
  1. CPU0 执行 a = 1, 发现 a 不在cache 里，因此发出 read-invalidate 信号， 然后将 a=1 写入 store buffer
  2. CPU1 执行 while (b == 0), 因为 CPU1 does not own b cache line, 因此发出 read 信号
  3. CPU0 执行 b = 1, 发现 own b cache line, 且状态为 M or E, 因此简单将 b = 1 写入自己的 cache line
  4. CPU0 接收到 read 信号， 将自己 b cache line 状态改为 S, 且打包 b cache line, 发出 read response
  5. CPU1 接收到 read response, 将 b cache line 写入自己 cache
  6. CPU1 执行 while (b == 0), 发现 b = 1, check 为假，因此跳出循环
  7. CPU1 执行 assert(a == 1), 读取 a cache line, 发现 a = 0, 则 asert fail
  8. CPU1 接收到 read-invalidate 信号， 将 a cache line invalidate, 并且发送 invalidate acknownledge 和 a cache line
  9. CPU0 接收到 invalidate acknownledge 和 response data, apply store buffer to received cache line
  
  上述执行顺序是可能的，由于 CPU1 未能及时处理 invalidation 信号，导致程序乱序
</details>

硬件本身解决不了上述问题，因为它不知道每个内存操作在多线程下的关系，因此硬件提供了内存屏障命令供软件开发者使用，以便告诉硬件各个内存操作之间的关系。
```java
void foo(void) {
  a = 1;
  //插入写屏障
  sfence();
  b = 1;
}

void bar(void) {
  while (b == 0) continue;
  assert (a == 1);
}
```
sfence 会强制 CPU flush its store buffer before applying each subsequent store to its variables's cache line. CPU 可以简单 stall 直到清空 store buffer, 也可以使用 store buffer hold subsequent stores util all of the prior entries in the store buffer had been applied.
<details>
  <summary>A possibility of sequence of operations with sfence</summary>
  
  假设 CPU0 执行 foo(), CPU1 执行 bar(). 且变量 a 只在 CPU1 cache 里，变量 b 只在 CPU0 cache 里。
  
  1. CPU0 执行 a = 1, 发现 a 不在 cache, 则发出 read-invalidate 信号， 并将 a = 1 写入 store buffer
  2. CPU1 执行 while (b == 0), 发出 read 信号
  3. CPU0 执行 sfence, 将现在 store buffer 中的所有 entry 打上标记
  4. CPU0 接收到 read 信号， 将 b cache line 状态改为 S, 打包发出。 此时 b 的值还是 0
  5. CPU0 执行 b = 1, 发现 store buffer 中还有标记的 entry, 因此将 b = 1 写入 store buffer, 且发出 invalidate 信号
  6. CPU1 接收到 read response, 将 b cache line 填充到自己 cache. 发现值仍是 0, 因此继续执行循环
  7. CPU1 接收到 read invalidate 信号， 将 a cache line invalidate, 然后打包发出, 且发出 invalidate acknownledge
  8. CPU1 接收到 invalidate 信号， 将 b cache line invalidate, 且发出 invalidate acknownledge
  9. CPU1 执行 while (b == 0), 发出 read 信号
  10. CPU0 接收到 a cache line 和 ack, 将 a=1 apply 到cache line, 且将 a cache line 状态改为 M
  11. CPU0 接收到 ack, 将 b = 1 写入 b cache line, 并将状态改为 M
  12. CPU0 接收到 read 信号，将 b cache line 状态改为 S, 且打包发出
  13. CPU1 接收到 read response, 将 b cache line 写入cache. b = 1, 跳出循环
  14. CPU1 发出 read 信号读取 a
  15. CPU0 接收到 read 信号，将 a cache line 状态改为 S, 并且打包发出
  16. CPU1 接收到 read response, 将 a 写入cache, 执行 assert (a == 1), success
  
  这里的区别在于 CPU0 写 b = 1时，并不是直接写入 cache line, 而是写入 store buffer, 并且必须等待 a = 1 applied 后才能 apply(a = 1 已被标记). 意思就是即使 CPU0 先接收到 b 的 invalidat acknownledge, 也必须等待，直到接收到 a 的 invalidate acknownledge. 在此之前， CPU1 的每次 read 仍然只能拿到 b = 0(note that 因为 b = 1 仍然在 store buffer 里，CPU0 响应 b 的 read 信号时，不会返回 b = 1, 而是直接返回 cache 中的值)
</details>

<details>
  <summary>Another possibility of sequence of operations with sfence</summary>
  
  假设 CPU0 执行 foo(), CPU1 执行 bar(). 且变量 a 只在 CPU1 cache 里，变量 b 只在 CPU0 cache 里。
  
  1. CPU0 执行 a = 1, 发现 a 不在 cache, 则发出 read-invalidate 信号， 并将 a = 1 写入 store buffer
  2. CPU1 执行 while (b == 0), 发出 read 信号
  3. CPU0 执行 sfence, 将现在 store buffer 中的所有 entry 打上标记
  4. CPU0 执行 b = 1, 发现 store buffer 中有标记的 entry, 因此将 b = 1 写入 store buffer. 此时 b cache line 状态为 E or M
  5. CPU0 接收到 read 信号， 将 b cache line 状态改为 S, 打包发出。 注意此时 cache line 中 b 的值仍是 0.
  6. CPU1 接收到 read response, 将 b cache line 写入cache, 由于 b = 0, 继续执行循环
  7. CPU1 接收到 read invalidate 信号， 将 a cache line invalidate, 然后打包发出, 且发出 invalidate acknownledge
  8. CPU0 接收到 a cache line 和 ack, 将 a=1 apply 到cache line, 且将 a cache line 状态改为 M
  9. 因为此时 store buffer 中已经没有标记的 operation, 因此可以 apply b=1 to cache
  10. 因为此时 b cache line 状态为 S, 因此 CPU0 发出 invalidate 信号
  11. CPU1 接收到 invalidate 信号， 将 b cache line invalidate, 并发出 invalidate acknownledge
  12. CPU0 接收到 ack, 将 b = 1 写入 b cache line, 并将状态改为 M
  13. CPU1 执行 while (b == 0), 发出 read 信号
  14. CPU0 接收到 read 信号，将 b cache line 状态改为 S, 且打包发出
  15. CPU1 接收到 read response, 将 b cache line 写入cache. b = 1, 跳出循环
  16. CPU1 发出 read 信号读取 a
  17. CPU0 接收到 read 信号，将 a cache line 状态改为 S, 并且打包发出
  18. CPU1 接收到 read response, 将 a 写入cache, 执行 assert (a == 1), success
  
</details>

## Invalidate queue
为了解决问题 #2， 引入 invalidate queue

![](image/store-buffer-invalidate-queue.png)



同理， 解决了主动发送信号端的效率问题，那么接收端 CPU 再接收到 invalidate 信号后也不是立即采取相应行动，而是把 invalidate 信号插入到一个 invalidaye queue 中，且立即返回 ACK 信号。 等待合适的时间，再去处理这个 queue 中的 invalidate.

store buffer 和 invalidate queue 虽然提升了 MESI 的性能，但是也引入了其它问题。
1. CPU 会尝试从 store buffer 中读取值，尽管它还没有提交，这个方案称之为 store forwarding.
2. store buffer 中的写入什么时候同步到 cache, 并没有保证
3. CPU 什么时候处理 invalidate queue 并没有保证

为了解决上述问题，又引入了 memory barrier


