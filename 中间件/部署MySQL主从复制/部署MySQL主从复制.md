# MySQL主从复制

## 主从复制原理

1.数据库有个bin-log二进制文件，记录了所有sql语句。

2.我们的目标就是把主数据库的bin-log文件的sql语句复制过来。

3.让其在从数据的relay-log重做日志文件中再执行一次这些sql语句即可。

**具体需要三个线程来操作：** 

​      1.binlog输出线程:每当有从库连接到主库的时候，主库都会创建一个线程然后发送binlog内容到从库。在从库里，当复制开始的时候，从库就会创建两个线程进行处理：

​      2.从库I/O线程:当START SLAVE语句在从库开始执行之后，从库创建一个I/O线程，该线程连接到主库并请求主库发送binlog里面的更新记录到从库上。从库I/O线程读取主库的binlog输出线程发送的更新并拷贝这些更新到本地文件，其中包括relay log文件。

​      3.从库的SQL线程:从库创建一个SQL线程，这个线程读取从库I/O线程写到relay log的更新事件并执行。

## 实践

### 1、环境准备

两台centos7环境，本次实验一台是本地虚拟机，一台是阿里云服务器。

### 2、安装MySQL

1、两个节点都安装mysql，检查系统是否安装其他版本的MYSQL数据

```bash
yum list installed | grep mysql
```

2、安装mysql56版本

```bash
wget http://repo.mysql.com/mysql-community-release-el7-5.noarch.rpm
rpm -ivh mysql-community-release-el7-5.noarch.rpm
yum install mysql-community-server -y  
```

3、启动mysql并设置开机自启

```bash
service mysqld start
systemctl enable mysqld
```

4、设置root密码

```bash
mysql_secure_installation
```

运行mysql_secure_installation会执行几个设置：

​    --为root用户设置密码

​    --删除匿名账号

​    --取消root用户远程登录

​    --删除test库和对test库的访问权限

​    --刷新授权表使修改生效

通过这几项的设置能够提高mysql库的安全，按照提示选择即可。

5、建立远程root用户

```bash
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED   BY 'yourpassword' WITH GRANT OPTION;
flush privileges;
```

6、测试mysql可以登录

```
mysql -uroot -pyourpassword -h 远程数据库ip
```

### 3、配置数据库主从

#### 1、配置主服务器

编辑主节点配置文件，添加启用二进制日志文件

```bash
vim /etc/my.cnf
```

添加：

```bash
log-bin=mysql-bin
server-id=1
```

![image-20211207172814848](https://gitee.com/huowolf/pic-md/raw/master/image-20211207172814848.png)

修改配置后，重启mysql

```bash
service mysqld restart
```

确认配置生效，

进入数据库，查看日志信息，查看二进制日志是否开启

![image-20211207173307025](https://gitee.com/huowolf/pic-md/raw/master/image-20211207173307025.png)

#### 2、配置从服务器

编辑主节点配置文件，添加启用二进制日志文件

```bash
log-bin=mysql-bin
relay-log=relay-log
relay-log-index=relay-log.index
server-id=12
```

![image-20211207173531835](https://gitee.com/huowolf/pic-md/raw/master/image-20211207173531835.png)

修改配置后，重启mysql。

进入mysql，查看从节点日志信息,查看中继日志是否开启:

```bash
show global variables like '%log%';
```

![image-20211207173854855](https://gitee.com/huowolf/pic-md/raw/master/image-20211207173854855.png)

#### 3、创建用于复制的用户

每个从库使用MySQL用户名和密码连接到主库，因此主库上必须有用户帐户，从库可以连接。任何帐户都可以用于此操作，只要它已被授予 **REPLICATION SLAVE**权限。可以选择为每个从库创建不同的帐户，或者每个从库使用相同帐户连接到主库。

```sql
GRANT REPLICATION SLAVE ON *.* TO 'replica'@'%' identified by 'x12345678';
flush privileges;
```

 一般不用root帐号，“%”表示所有客户端都可能连，只要帐号，密码正确，此处可用具体**客户端IP**代替，如192.168.245.139，加强安全。

 replica，即创建的用户名，x12345678即对应的密码。

#### 4、设置主从复制关系

先查看主库状态

![image-20211207162132263](https://gitee.com/huowolf/pic-md/raw/master/image-20211207162132263.png)

然后在从节点上进入mysql中，执行下面命令：

```sql
CHANGE MASTER TO \
MASTER_HOST='118.31.35.39', \
MASTER_USER='replica', \
MASTER_PASSWORD='x12345678', \
MASTER_LOG_FILE='mysql-bin.000001', \
MASTER_LOG_POS=739;
```

最后启动复制

```
start slave;
```

#### 5、查看复制状态

```bash
show slave status\G
```

![image-20211207174658918](https://gitee.com/huowolf/pic-md/raw/master/image-20211207174658918.png)

这两个yes必须同时出现。即为ok。

## 4、测试主从复制

在主节点上创建一个数据库

```
create database test;
```

查看主节点二进制日志：

```sql
show master log
```

![image-20211207175119609](https://gitee.com/huowolf/pic-md/raw/master/image-20211207175119609.png)

查看从节点二进制日志，查看是否复制成功：

![image-20211207175308594](https://gitee.com/huowolf/pic-md/raw/master/image-20211207175308594.png)

查看从节点是否有test数据库：

![image-20211207175349875](https://gitee.com/huowolf/pic-md/raw/master/image-20211207175349875.png)

如图，主从复制即是ok的。

## 参考

1、https://cloud.tencent.com/developer/article/1458934

2、https://segmentfault.com/a/1190000010867488