Introduction to MESI

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

cache entry 的状态可以根据不同的事件进行更新。

Event Name  | Comment
------------| -------
PrRd | processor request to read a cache entry
PrWe | processor request to write a cache entry
BusRd | another processor request to read a cache entry
BusRdX | another processor which does not have the entry in the cache request to write the cache entry
BusUpgr | another processor which have the entry in cache request to write the cache entry
Flush | an entire cache entry is written back to main memory by another processor
Flushopt | an entire block is posted on the bus in order to supply it to another processor(cache to cache transfer)

M 和 E 的状态总是精确的， 但是S不是。 当其它 S cache entry 被它们的cache discard时，它们并不会通知当前 cache, 则当前的cache entry事实上是 Exclusive，但是它的状态仍然是S。

处于不同状态的 cache entry 在处理自己处理器 event 时的 response 如下表所示
![](image/mesi-change.png)

处于不同状态的 cache entry 在处理其它处理器发出的 event 时的response 如下表所示
![](image/mesi-recieve.png)

An example to show how MESI works
![](image/mesi-example.png)

1. 首先 P1 发出 PrRd, 发现cache 中没有，则给 bus 发出 BusRd. 因为其它 cache 都没有该 cache line, 则将状态改为 E, 并且通过 memory controller 从主存中读取数据。
2. P1 发出 PrWr, 将当前 cache entry 状态改为 M
3. P3 发出 PrRd, 发现自己没有该 cache entry, 则发出 BusRd, P1 接收到该信息后，将自己的 cache entry 标记为 S,且通过 memory controller 将 cache entry 同步到主存，并且将cache entry 发给 P3
4. P3 发出 PrWr, 将状态改为 M, 并且发出 BusUpgr. P1 接收到后，将自己的 cache entry 标记为 I
5. P1 发出 PrRd, 发现是 Invalid, 则发出 BusRd, P3 接收到后，将自己的 cache entry 标记为 S, 通过 memory controller 写回内存，并且通过 bus 发给 P1
6. P3 发出 PrRd, 发现自己 cache entry 是 S, 则直接读取返回
7. P2 发出 PrRd, 然后发出 BusRd, P1 或者 P3 接收到这个事件，将自己的 cache entry 发给 P2

MESI protocol 也有自己的缺点， 当 write/read invalid cache entry 时，需要等待很久，去查询其它 core 中该cache entry 的状态。 并且当需要 invalidate cache entry in other cores, 也需要花费很长时间。为了解决这些问题， 引入 store buffer 和 invalidate queue

![](image/store-buffer-invalidate-queue.png)

为了避免对 P1 cache line 的写入需要等待其它处理器先 invalidate 自己的cache line 再返回 ACK 信号，引入了 store buffer. P1 可以先把 write 写入 store buffer, 发出 read-invalidate 信号后继续执行之后的命令，无需等待该信号返回。这样就可以节省很多 CPU 之间通信的时间。然后异步等待其它 CPU 的响应， 当响应时，将store buffer apply to cache.

同理， 解决了主动发送信号端的效率问题，那么接收端 CPU 再接收到 invalidate 信号后也不是立即采取相应行动，而是把 invalidate 信号插入到一个 invalidaye queue 中，且立即返回 ACK 信号。 等待合适的时间，再去处理这个 queue 中的 invalidate.

store buffer 和 invalidate queue 虽然提升了 MESI 的性能，但是也引入了其它问题。
1. CPU 会尝试从 store buffer 中读取值，尽管它还没有提交，这个方案称之为 store forwarding.
2. store buffer 中的写入什么时候同步到 cache, 并没有保证
3. CPU 什么时候处理 invalidate queue 并没有保证

为了解决上述问题，又引入了 memory barrier


