## Redis集群的选举原理

Redis集群的经典架构：多主多从，至少是3主3从。

Redis集群各节点之间的通信协议：gossip协议。

gossip协议：各个节点之间都会保持通信，当某一个节点挂掉或者新增的时候，与它相邻的节点就会感知到，这时候此节点就是失去链接或者创建链接。

![img](https://gitee.com/huowolf/pic-md/raw/master/20200526161729273.png)

当slave（从节点）发现自己的master（主节点）不可用时，便尝试进行Failover，以便成为新的master。由于挂掉的master可能有多个slave，从而存在多个slave竞争成为master节点的过程，过程如下：

1、slave发现自己的master不可用；

2、slave将记录集群的currentEpoch（选举周期）加1，并广播FAILOVER_AUTH_REQUEST 信息进行选举；

3、其他节点收到FAILOVER_AUTH_REQUEST信息后，只有其他的master可以进行响应，master收到消息后返回FAILOVER_AUTH_ACK信息，对于同一个Epoch，只能响应一次ack；

4、slave收集master返回的ack消息。

5、slave判断收到的ack消息个数是否大于半数的master个数，若是，则变成新的master。

6、广播Pong消息通知其他集群节点，自己已经成为新的master。

注意：从节点并不是在主节点一进入 FAIL 状态就马上尝试发起选举，而是有一定延迟，一定的延迟确保我们等待FAIL状态在集群中传播，slave如果立即尝试选举，其它masters或许尚未意识到FAIL状态，可能会拒绝投票。

## 参考

https://www.codenong.com/cs106356837/