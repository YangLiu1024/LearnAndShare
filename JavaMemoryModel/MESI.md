Introduction to MESI

The MESI protocol is an invalidate-based cache coherence protocol, and is one of the most common protocol which support write-back caches.
1. Modified(M): only valid in current cache, and is dirty, other cache can not access its responding cache line
2. Exclusive(E): only valid in current cache, but is clean
3. Shared(S): be stored in other caches, and is clean
4. Invalid(I): invalid

对于M状态， 因为 MESI 支持 write-back, 这个dirty 的 cache entry 会在之后的某个时间点写回主存。如果在此期间， 有其它的 core 要访问同一 cache entry 地址， 当前 core 会接收到这一事件，并且将dirty 的cache entry 写回到主存，并且将该 cache line 标记为 shared.
对于E状态， 如果其它 core 要读这个 cache line, 则将状态改为 share. 如果当前core 要写该 cache line, 则将状态改为 M.
对于S状态，如果其它 core 修改了它的 cache line, 则当前 core 的该cache line 会被标记为 Invalid
对于I状态，表示当前cache entry invalid 且 unused, 可以被优先 replace
