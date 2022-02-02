## 为啥HashMap的长度是2的n次方？

加快哈希计算以及减少哈希冲突。

#### 加快hash计算

当容量是2^n时，h & (length-1) = h % length

#### 减少hash冲突

length=2^n为偶数，length-1为奇数，奇数的二进制最后一位为1，这样便保证了hash&(length-1)的最后一位可能为0，也可能为1（这取决于h的值），即 & 运算的结果可能是偶数，也可能是奇数，这样保证了散列的均匀性。



## 参考

https://zhuanlan.zhihu.com/p/91636401



