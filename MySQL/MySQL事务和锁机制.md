1、怎么理解InnoDB引擎中的事务？

事务可以使【**一组操作**】要么全部成功，要么全部失败。事务其目的是为了【**保证数据最终的一致性**】。

2、了解事务的几大特性吗？

ACID，分别是原子性（Atomicity）、一致性（Consistency）、隔离性（Isolation）、持久性（Durability）。

##### 原子性

​		原子性指的是：当前事务的操作要么同时成功，要么同时失败。原子性由undo log日志来保证，因为undo log记载着数据修改前的信息。

​		比如我们要insert一条数据，那undo log会记录一条对应的delete日志。我们要update一条记录时，那undo log会记录之前的旧值的update记录。

​		如果执行事务过程中出现异常的情况，那执行回滚。InnoDB引擎就是利用undo log记录下的数据，来将数据恢复到事务开始之前。

##### 隔离性

​		隔离性指的是：在事务并发执行时，他们内部的操作不能互相干扰。如果多个事务可以在同一个时刻操作同一份数据，那么就可能会产生脏读、重复读、幻读的问题。

​		于是事务之间需要存在一定的隔离。在InnoDB引擎中，定义了四种隔离级别供我们使用，分别是：read uncommit（读未提交）、read commit（读已提交）、repeatable read（可重复读）、serializable（串行化）。

​		不同的隔离级别对事务之间的隔离性是不一样的，级别越高事务隔离性越好，但性能就越低。隔离性是由MySQL的各种锁来实现的，只是它屏蔽了加锁的细节。

##### 持久性

​		持久性指的是：一旦提交了事务，它对数据库的修改就应该是永久性的。说白了就是，它会将数据持久化在磁盘上。

​		而持久性是由redo log日志来保证，当我们要修改数据时，MySQL是先把这条记录所在的**页**找到，然后把该页加载到内存中，将对应记录进行修改。

​		为了防止内存修改完了，MySQL就挂掉了（如果内存改完，直接挂掉，那这次的修改相当于丢失了），MySQL引入了redo log，内存写完了，然后会写一份redo log，这份redo log记载着这次在某个页上做了什么修改。即便MySQL在中途挂了，我们还可以根据redo log来对数据进行恢复。

​		redo log是顺序写的，写入速度很快。并且它记录的是物理修改，文件的体积很小，恢复速度也很快。

##### 一致性

​		一致性可以理解成我们使用事务的目的，而隔离性，原子性，持久性均是为了保证一致性的手段，保证一致性需要由应用程序代码来保证。

​		比如，如果事务在发生的过程中，出现了异常情况，此时你得回滚事务，而不是强行提交事务来导致数据不一致。

3、MySQL锁机制？

在InnoDB引擎下，按锁的粒度分类，可以简单分为行锁和表锁。

行锁实际上是作用在索引之上的。当我们的SQL命中了索引，那锁住的就是命中条件内的索引节点（这种就是行锁），如果没有命中索引，那我们锁住的就是整个索引数（表锁）。

简单来说就是：锁住的整棵树还是某几个节点，完全取决于SQL条件是否有命中到对应的索引节点。

而行锁又可以简单分为读锁（共享锁、S锁）和写锁（排它锁、X锁）。

读锁是共享的，多个事务可以同时读取同一个资源，但不允许其他事务修改。写锁是排他的，写锁会阻塞其他的写锁和读锁。

4、隔离级别

首先来说下read uncommit（读未提交）。比如说：A向B转钱，A执行了转账语句，但A还没有提交事务，B读取数据，发现账户钱变多了。B跟A说，我已经收到钱了。A回滚事务，等B再查看账户的钱时，发现并没有多。

简单的定义就是：事务B读取到了事务A还没提交的数据，这种用专业术语来说，叫做脏读。

对于锁的维度而言，其实就是在read uncommit隔离级别下，读不会加任何锁，而写会加排它锁。读什么锁都不加，这就让排他锁无法排它了。

而我们又知道，对于更新操作而言，InnoDB是肯定会加写锁的（数据库是不可能允许在同一时间，更新同一条记录的）。而读操作，如果不加任何锁，那么就会造成上面的脏读。

脏读在生产环境下肯定是无法接受的，那如果读加锁的话，那意味着：当更新数据时，就没有办法读取了，这会极大地降低数据库性能。

在MySQL InnoDB引擎层面，又有新的解决方案（解决加锁后读写性能问题），叫做MVCC（Multi-Version Concurrency Control）多版本并发控制。

在MVCC下，就可以做到读写不阻塞，且避免了类似脏读的问题。那MVCC是怎么做的那？

MVCC通过生成数据快照（Snapshot），并用这个快照来提供一定级别（语句级或事务级）的一致性读取。

回到事务隔离级别下，针对于read commit（读已提交）隔离级别，它生成的就是语句级快照，而针对repeatable read（可重复读），它生成的就是事务级的快照。

前面提到过read uncommit隔离级别下会产生脏读，而read commit隔离级别解决了脏读。

思想其实很简单：在读取的时候生成了一个“版本号”，等到其他事务commit了之后，才会读取最新已commit的“版本号”数据。

比方说：事务A读取了记录（生成版本号），事务B修改了记录（此时加了写锁），事务B修改了记录（此时加了写锁），事务A再读取的时候，是依据最新的版本号来读取的（当事务B执行commit了之后，会生成一个新的版本号），如果事务B还没有commit，那么事务A读取的还是之前版本号的数据。

通过版本的概念，这样就解决了脏读的问题，而通过版本又可以对应快照的数据。

read commit（读已提交）解决了脏读，但也会有其他并发的问题。【不可重复读】：一个事务读取到另外一个事务已经提交的数据，也就是说一个事务可以看到其他事务所做的修改。

不可重复读的例子：A查询数据库得到数据，B去修改数据库的数据，导致A多次查询数据库的结果都不一样。【危害：A每次查询的结果都是受B的影响】

了解MVCC之后，就很容易想到repeatable read（可重复读）隔离级别是怎么避免不可重复读的问题了。

repeatable read（可重复读）隔离级别是**事务级别**的快照，每次读取的都是当前事务的版本，即使当前数据被其他事务修改了（commit），也只会读取当前事务版本的数据。

在InnoDB引擎下的repeatable read（可重复读）隔离级别下，在MVCC下，快照读，已经解决了幻读的问题（因为它是读历史版本的数据）。

而如果是当前读（比如 select * from table for update），则需要配合间隙锁来解决幻读的问题。

剩下的就是serializable（串行）隔离级别了，它是最高的隔离级别，相当于不允许事务的并发，事务与事务之间执行时串行的，它的效率最低，但同时也是最安全的。

5、MVCC原理

MVCC主要通过read view和undo log来实现的。

undo log，它会记录修改数据之前的信息，事务中的原子性就是通过undo log来实现的。所以，有undo log可以帮我们找到**版本**的数据。

而read view实际上就是在查询时，InnoDB会生成一个read view，read view有几个重要的字段，分别是：

* `m_ids`：表示在生成`ReadView`时当前系统中活跃的读写事务的`事务id`列表。
* `min_trx_id`：表示在生成`ReadView`时当前系统中活跃的读写事务中最小的`事务id`，也就是`m_ids`中的最小值。
* `max_trx_id`：表示生成`ReadView`时系统中应该分配给下一个事务的`id`值。

* `creator_trx_id`：表示生成该`ReadView`的事务的`事务id`。

MVCC其实就是靠**比对版本**来实现读写不阻塞，而版本的数据存在于undo log中。

而针对于不同的隔离级别（read commit和repeatable read），无非就是read commit隔离级别下，每次都获取一个新的read view，repeatable read隔离级别则每次事务只获取一个read view。