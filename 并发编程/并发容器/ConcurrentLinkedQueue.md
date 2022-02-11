# ConcurrentLinkedQueue

ConcurrentLinkedQueue 是一个基于链接节点的无界线程安全队列， 它采用先进 先出的规则对节点进行排序， 当我们添加一个元素的时候， 它会添加到队列的尾部； 当我们获取一个元素 时， 它会返回队列头部的 元素。 它 采用 了“ wait- free” 算法（ 即 CAS 算法）来 实现。

## 　 ConcurrentLinkedQueue 的 结构

通过 ConcurrentLinkedQueue 的类图来分析一下它的结构，

![image-20220211135124034](https://gitee.com/huowolf/pic-md/raw/master/image-20220211135124034.png)

ConcurrentLinkedQueue 由 head 节点 和 tail 节点 组成， 每个 节点（ Node）由 节点 元素（ item）和 指向 下一个 节点（ next）的 引用 组成， 节点与节点之间就是通过这个 next 关联 起来， 从而组成一张链表结构的队列。 默认情况下 head 节点存储的元素为 空， tail 节点等于 head 节点。

## 主要属性和内部类

```java
// 链表头节点
private transient volatile Node<E> head;
// 链表尾节点
private transient volatile Node<E> tail;
```

```java
private static class Node<E> {
    volatile E item;
    volatile Node<E> next;
}
```

## 主要构造方法

```java
public ConcurrentLinkedQueue() {
    // 初始化头尾节点
    head = tail = new Node<E>(null);
}

public ConcurrentLinkedQueue(Collection<? extends E> c) {
    Node<E> h = null, t = null;
    // 遍历c，并把它元素全部添加到单链表中
    for (E e : c) {
        checkNotNull(e);
        Node<E> newNode = new Node<E>(e);
        if (h == null)
            h = t = newNode;
        else {
            t.lazySetNext(newNode);
            t = newNode;
        }
    }
    if (h == null)
        h = t = new Node<E>(null);
    head = h;
    tail = t;
}
```



## 入队列

规则：第一是将入队节点设置成当前队列尾节点的下一个节点。第二是更新tail节点，如果tail节点的next节点不为空，则将入队节点设置成tail节点，如果tail节点的next节点为空，则将入队节点设置成tail的next节点，所以tail节点不总是尾节点，理解这一点很重要。

多线程入队规则：如果有一个线程正在入队，那么它必须先获取尾节点，然后设置尾节点的下一个节点为入队节点，但这时可能有另外一个线程插队了，那么队列的尾节点就会发生变化，这时当前线程要暂停入队操作，然后重新获取尾节点

```java
public boolean offer(E e) {
    // 如果e为null，则直接抛出NullPointerException异常
    checkNotNull(e);
    // 创建入队节点
    final Node<E> newNode = new Node<E>(e);
 
    // 循环CAS直到入队成功
    // 1、根据tail节点定位出尾节点（last node）；
    // 2、将新节点置为尾节点的下一个节点；
    // 3、casTail更新尾节点
    for (Node<E> t = tail, p = t;;) {
        // p用来表示队列的尾节点，初始情况下等于tail节点
        // q是p的next节点
        Node<E> q = p.next;
        // 判断p是不是尾节点，tail节点不一定是尾节点，判断是不是尾节点的依据是该节点的next是不是null
        // 如果p是尾节点
        if (q == null) {
            // p is last node
            // 设置p节点的下一个节点为新节点，设置成功则casNext返回true；
            // 否则返回false，说明有其他线程更新过尾节点
            if (p.casNext(null, newNode)) {
                // 如果p != t，则将入队节点设置成tail节点，
                // 更新失败了也没关系，因为失败了表示有其他线程成功更新了tail节点
                if (p != t) // hop two nodes at a time
                    casTail(t, newNode);  // Failure is OK.
                return true;
            }
        }
        // 多线程操作时候，由于poll时候会把旧的head变为自引用，然后将head的next设置为新的head
        // 所以这里需要重新找新的head，因为新的head后面的节点才是激活的节点
        else if (p == q){
            p = (t != (t = tail)) ? t : head;   //好难懂啊。。。？？？
        // 寻找尾节点
        else
            p = (p != t && t != (t = tail)) ? t : q;
    }
}
```

