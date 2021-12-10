## 编译Redis可执行文件

1、获取源码包

https://redis.io/download

```bash
wget https://download.redis.io/releases/redis-6.2.6.tar.gz
```

2、解压

```bash
tar xzvf redis-6.2.6.tar.gz
```

3、编译与安装

```
cd redis-6.2.6

make

cd src

make install PREFIX=/usr/local/redis
```

4、将redis-cli拷贝到bin下，让redis-cli指令可以在任意目录下直接使用

```bash
cp /usr/local/redis/bin/redis-cli /usr/local/bin/
```

## 运行三主三从，6个节点

1、创建好工作目录

```bash
mkdir cluster-test
cd cluster-test
mkdir 7000 7001 7002 7003 7004 7005
```

2、创建redis.conf文件

```bash
port 7000
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
daemonize yes
logfile redis.log
loglevel verbose
```

3、将redis-server可执行文件和redis.conf给每个目录拷贝一份，并修改端口号。

修改端口号：

```bash
sed -i  's/7000/7001/g' 7001/redis.conf
```

 此时的目录结构

```bash
[root@localhost cluster-test]# tree
.
├── 7000
│   ├── redis.conf
│   └── redis-server
├── 7001
│   ├── redis.conf
│   └── redis-server
├── 7002
│   ├── redis.conf
│   └── redis-server
├── 7003
│   ├── redis.conf
│   └── redis-server
├── 7004
│   ├── redis.conf
│   └── redis-server
├── 7005
│   ├── redis.conf
│   └── redis-server

```

4、依次启动各个节点

如：

```bash
cd 7000
./redis-server ./redis.conf
```

5、检查redis的各个进程

```bash
ps -ef | grep redis
```

![image-20211206135427024](https://gitee.com/huowolf/pic-md/raw/master/image-20211206135427024.png)

如图，即是ok的。

## 初始化Redis集群（将hash槽指派给节点）

Redis 5以上版本使用如下命令方便的初始化集群

```bash
redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 \
127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 \
--cluster-replicas 1
```

![image-20211206152303170](https://gitee.com/huowolf/pic-md/raw/master/image-20211206152303170.png)

输入yes后

![image-20211206152424958](https://gitee.com/huowolf/pic-md/raw/master/image-20211206152424958.png)

## 测试一把Redis集群

```bash
 redis-cli -c -p 7000
```

-c ：以集群模式连接redis集群

-p：指定连接的端口

1、打印集群的信息

```
127.0.0.1:7000> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:6
cluster_size:3
cluster_current_epoch:6
cluster_my_epoch:1
cluster_stats_messages_ping_sent:933
cluster_stats_messages_pong_sent:925
cluster_stats_messages_sent:1858
cluster_stats_messages_ping_received:920
cluster_stats_messages_pong_received:933
cluster_stats_messages_meet_received:5
cluster_stats_messages_received:1858
```

2、测试集群

```bash
127.0.0.1:7000> set foo bar
-> Redirected to slot [12182] located at 127.0.0.1:7002
OK
127.0.0.1:7002> set hello world
-> Redirected to slot [866] located at 127.0.0.1:7000
OK
127.0.0.1:7000> get foo
-> Redirected to slot [12182] located at 127.0.0.1:7002
"bar"
127.0.0.1:7002> get hello
-> Redirected to slot [866] located at 127.0.0.1:7000
"world"
```

## 参考

1、https://segmentfault.com/a/1190000023364209

2、http://www.redis.cn/topics/cluster-tutorial.html

3、https://redis.io/topics/cluster-tutorial

