# Java 并发容器
Java 里有很多高效的并发容器和框架。
# ConcurrentHashMap
## Why not HashMap
HashMap 在并发条件下，是线程不安全的，这个要从 HashMap 的底层数据结构说起。  
JDK7 里， HashMap 的底层结构很简单，就是一个数组，每个数组都是一个链表。   
通过 key 来计算 hashcode, 通过 hashcode 判断插入位置，如果发生hash 冲突，则使用链表将具有相同 hash 值的 entry 存放在链表上，最新加入的 entry 会放在数组元素里，它的 next 指向之前存在数组元素里的 entry.  
在数组容量到达一定界限，会触发扩容，数组大小会翻倍，所有元素会挪到新数组上。   
JDK8 中，为了解决当发生大量 hash 冲突时，get 的复杂度会变成 O(N), 将链表改为红黑树，红黑树的查找复杂的为 O(logN).   
那么为什么 HashMap 在多线程下就是不安全的呢？   
### resize 死循环
HashMap 的初始容量为16, 当有数据插入时，都会检查容量是否达到阈值，如果达到，则要增大数组的大小。但是这样以来，因为数组大小改变，所有 bucket 的位置都要重新计算(通过 (table.length - 1) & hash 来计算数组下标)。这叫 rehash, 成本相当的大。  
```js
void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
 
        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        table = newTable;
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
}

void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                int i = indexFor(e.hash, newCapacity);//这几行代码会改变链表的结构，在多线程情况下，如果两个线程都执行 put, 且触发了 resize, 那么就很可能造成环形链表，从而导致 entry 的 next永不为 null, 从而导致死循环
                e.next = newTable[i];
                newTable[i] = e;//且 transfer 之后，链表的元素顺序是颠倒了的，之所以这样，是为了保证效率，因为插在头部是 O(1), 如果插在尾部，则为 O(N)
                e = next;
            }
        }
}
```
我们假设 hash map 大小为2， 现有数据 5, 7, 3 依次插入，那么三个 entry 都会插入到 table[1] 位置上，且 table[1] 存放 key(3), key(3).next 为 key(7), key(7).next 为 key(5), key(5) 的 next 为 null.   
现在有两个线程，都要 put, 那么 hashmap 就需要 resize, 大小将为 4.  假设线程1 执行到 Entry<K,V> next = e.next 时挂起，此时 e 指向 key(3)， next 指向 key(7). 线程2 开始执行。  

那么对于线程2，首先，e 指向了key(3), next 指向了 key(7), 再 rehash 后，e.next = newTable[3], 此时 newTable[3] 为 null, 所以 key(3) 的 next 指向了 null, 然后 newTable[3] = e, 进入下一步迭代。其实这个时候，如果有另一个线程想 get key(5), 就已经会返回 null 了，因为 key(3) 的 next 已经被改为了 null.   
进入下一次迭代时， e指向了 key(7), next 指向了 key(5), 然后 key(7) 的 next 会重新指向 newTable[3], 即 newTable 里的 key(3), newTable[3] 又指向了 key(7). 进入下一次迭代。   
此时 e 指向了 key(5), next 指向了 null. key(5) 的 next 会指向 newTable[1], 即 null, 然后 newTable[1] 指向了 key(5). 这个时候，线程2 新生成的 hash map 的状态是， newTable[1] 为 key(5), key(5).next 为 null. newTable[3] 为 key(7), key(7).next 为 key(3), key(3).next 为 null.    

现在线程1 恢复执行。 对于线程1，e.next = newTable[3], 线程1 的 newTable[3] 现在还是 null, 所以 key(3) 的 next 被设为 null, newTable[3] 被设置为 key(3). 现在看起来还没什么问题，进入下一步迭代。   
e 指向 key(7), key(7) 的 next 已经在线程 1 里改为指向了 key(3), 所以这里 next 指向 key(3). e.next = newTable[3], 这里 newTable[3] 指向 key(3), 所以 key(7) 的 next 又指向了 key(3), newTable[3] 指向 key(7), 进入下一步迭代。   
key(3) 的 next 在线程1 里已经改为 null, e.next = newTable[3], 那么 key(3) 的 next 又会指向 key(7). 这时 key(3) 和 key(7) 形成环形链表, 且 key(5) 已经被丢失了。


