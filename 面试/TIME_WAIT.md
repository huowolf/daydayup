## 大量的Time_WAIT状态的TCP连接，对业务有什么影响？怎么处理？

几个方面：

1、问题描述：什么现象？什么影响？

2、问题分析

3、解决方案

4、底层原理

## 1、问题描述

高并发常见下，TIME_WAIT连接存在，属于正常现象。

大量的TIME_WAIT状态TCP连接，有什么业务上的影响吗？

Nginx作为反向代理时，大量的短链接，可能导致Nginx的TCP连接处于time_wait状态：

* 每一个time_wait，都会占用一个本地端口，上限为65535。
* 当大量的连接处于time_wait时，新建立TCP连接会出错，address already in use:connect 异常。

统计 TCP连接的状态：

```shell
// 统计：各种连接的数量
$ netstat -n | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'
ESTABLISHED 1154
TIME_WAIT 1645
```

## 2、问题分析

大量的TIME_WAIT状态TCP连接存在，其本质原因是什么？

* 大量的短链接存在
* 特别是HTTP请求中，如果connection头部取值被设置为close时，基本上都由服务端发起主动关闭连接。
* 而，TCP四次挥手关闭连接机制中，为了保证ACK重发和丢弃延迟数据，设置time_wait为2倍的MSL（报文最大存活时间）

TIME_WAIT状态：

* TCP连接中，主动关闭一方出现的状态。（收到FIN命令，进入TIME_WAIT状态，并返回ACK命令）
* 保持2个MSL时间，即4分钟。（MSL为2分钟）

## 3、解决办法

解决上述time_wait状态大量存在，导致新连接创建失败的问题，一般解决办法：

1、客户端

HTTP请求的头部，connection设置为keep-alive，保持存活一段时间；现在的浏览器，一般都这么进行了。

2、服务器端

* 允许time_wait状态的socket被重用
* 缩减time_wait时间，设置为1MSL（即，2mins）

## TIME_WAIT存在的必要性

1、TCP连接建立后，**主动关闭连接**的一端，收到对方的FIN请求后，发送ACK响应，会处于TIME_WAIT状态。

2、TIME_WAIT状态，存在的必要性：

* **可靠的TCP全双工连接的终止**：四次挥手关闭TCP连接过程中，最后的ACK是由主动关闭连接的一端发出的，如果这个ACK丢失，则，对方会重发FIN请求，因此，在主动关闭连接的一端，需要维护一个time_wait状态，处理对方重发的FIN请求。
* **处理延迟到达的报文**：由于路由器可能抖动，TCP报文会延迟到达，为了避免**延迟到达的报文**被误认为是新TCP连接的报文，则，需要在允许新创建TCP连接之前，保持一个不可用的状态，等待所有延迟报文的消失，一般设置为2倍的MSL（报文的最大生存时间），解决延迟到达的TCP报文问题。

## 参考

https://www.cnblogs.com/javastack/p/15540385.html
