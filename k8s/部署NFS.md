# 搭建 NFS 服务

## 配置要求

两台 linux 服务器，centos 7

- 一台用作 nfs server   （本次实验采用阿里云上的轻量级云服务器）
- 另一台用作 nfs 客户端

## 安装NFS Server

* 执行以下命令安装 nfs 服务器所需的软件包

```bash
yum install -y rpcbind nfs-utils
```

* 查看nfs服务是否安装正常

```
rpcinfo -p localhost
```

或者是通过公网测试服务端的nfs服务是否可用。

```
rpcinfo -p 139.96.6.20
```

显示如下即正常

```
   program vers proto   port  service
    100000    4   tcp    111  portmapper
    100000    3   tcp    111  portmapper
    100000    2   tcp    111  portmapper
    100000    4   udp    111  portmapper
    100000    3   udp    111  portmapper
    100000    2   udp    111  portmapper
    100005    1   udp    892  mountd
    100005    1   tcp    892  mountd
    100005    2   udp    892  mountd
    100005    2   tcp    892  mountd
    100005    3   udp    892  mountd
    100005    3   tcp    892  mountd
    100003    3   tcp   2049  nfs
    100003    4   tcp   2049  nfs
    100227    3   tcp   2049  nfs_acl
    100003    3   udp   2049  nfs
    100003    4   udp   2049  nfs
    100227    3   udp   2049  nfs_acl
    100021    1   udp  48004  nlockmgr
    100021    3   udp  48004  nlockmgr
    100021    4   udp  48004  nlockmgr
    100021    1   tcp  45652  nlockmgr
    100021    3   tcp  45652  nlockmgr
    100021    4   tcp  45652  nlockmgr
```

### 将mountd进程运行在固定端口

vim /etc/sysconfig/nfs , 取消注释

![image-20211203141212797](https://gitee.com/huowolf/pic-md/raw/master/image-20211203141212797.png)

### 阿里云远程服务器开放端口

服务器端防火墙开放111、892、2049的 tcp / udp 允许

![image-20211203142128795](https://gitee.com/huowolf/pic-md/raw/master/image-20211203142128795.png)

### 共享配置

创建共享目录

```bash
mkdir /root/nfs_root
```

执行命令 `vim /etc/exports`，创建 exports 文件，文件内容如下：

```bash
/root/nfs_root/ *(insecure,rw,sync,no_root_squash)
```

### 启动nfs服务

```bash
systemctl enable rpcbind
systemctl enable nfs-server

systemctl start rpcbind
systemctl start nfs-server
exportfs -r
```

检查配置是否生效

```bash
exportfs
# 输出结果如下所示
/root/nfs_root /root/nfs_root
```

## 在客户端测试nfs

* 执行以下命令安装 nfs 客户端所需的软件包

  ```bash
  yum install -y nfs-utils
  ```

* 执行以下命令检查 nfs 服务器端是否有设置共享目录

  ```bash
  # showmount -e $(nfs服务器的IP)
  showmount -e 172.17.216.82
  # 输出结果如下所示
  Export list for 172.17.216.82:
  /root/nfs_root *
  ```

* 执行以下命令挂载 nfs 服务器上的共享目录到本机路径 `/root/nfsmount`

  ```bash
  mkdir /root/nfsmount
  # mount -t nfs $(nfs服务器的IP):/root/nfs_root /root/nfsmount
  mount -t nfs 172.17.216.82:/root/nfs_root /root/nfsmount
  # 写入一个测试文件
  echo "hello nfs server" > /root/nfsmount/test.txt
  ```

* 在 nfs 服务器上执行以下命令，验证文件写入成功

  ```bash
  cat /root/nfs_root/test.txt
  ```

## 参考

1、http://press.demo.kuboard.cn/learning/k8s-intermediate/persistent/nfs.html

2、https://zhuanlan.zhihu.com/p/302774003

3、https://www.cnblogs.com/whych/p/9196537.html

