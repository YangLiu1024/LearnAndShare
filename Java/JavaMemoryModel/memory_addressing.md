按字节编址，指的是存储空间的最小编址单位是字节；按字编址，是指存储空间的最小编址单位是字。
举例来说，一个存储器的字长为 32 bit, 容量为 1 MB, 如果按照字节编址，那么需要 2 ^ 20 B/1 B = 2 ^ 20, 也就是需要 20 根地址线才能完成
对存储器容量空间的编码。 如果按照字编址，那么需要 2 ^ 20B/4B = 2 ^ 18, 即需要 18 根地址线。如果按字节编址，则会有更多的地址编码，寻址范围也更大。