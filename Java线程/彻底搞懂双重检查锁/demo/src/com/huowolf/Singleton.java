package com.huowolf;

/**
 * 双重检查锁需要搭配volatile
 * volatile可以避免指令重排序。
 *
 * 1、何谓双重检查锁？
 * 先判断对象有没有被初始化，再决定要不要加锁。
 * 所以上来检查对象是否为null。
 *
 * 如果有多个线程同时通过了第一次检查，并且其中一个线程首先通过了第二次检查并实例化了对象
 * 那么第二重检查就保证了剩余通过第一重检查的线程就不会再去实例化对象。
 *
 * 这样，除去初始化的时候会出现加锁的情况，后续所有的调用都会避免加锁而直接返回，解决了性能消耗问题。
 *
 * 2、双重检查锁为啥需要搭配volatile?
 *
 * 实例化对象的操作并不是原子性的，可以分为三个步骤：
 * 1）分配内存空间
 * 2）初始化对象
 * 3）将对象指向刚分配的内存空间
 *
 * 编译器可能会对第二步和第三步进行指向重排序，顺序就变成了：
 * 1）分配内存空间
 * 2）将对象指向刚分配的内存空间
 * 3）初始化对象
 *
 * volatile可以避免指令重排序。
 *
 * 延伸：volatile如何禁止指令重排序？
 * 通过在指令间插入内存屏障来告诉编译器和CPU禁止指令重排序
 * 屏障类型：
 * StoreStore
 * StoreLoad
 * LoadLoad
 * LoadStore
 */
public class Singleton{

    private static volatile Singleton singleton;

    private Singleton(){}

    public static Singleton getSingleton() {
        if (singleton == null){
            synchronized (Singleton.class){
                if (singleton == null){
                    singleton = new Singleton();
                }
            }
        }

        return singleton;
    }
}


/**
 * https://learn.lianglianglee.com/%E4%B8%93%E6%A0%8F/Java%20%E5%B9%B6%E5%8F%91%E7%BC%96%E7%A8%8B%2078%20%E8%AE%B2-%E5%AE%8C/63%20%E5%8D%95%E4%BE%8B%E6%A8%A1%E5%BC%8F%E7%9A%84%E5%8F%8C%E9%87%8D%E6%A3%80%E6%9F%A5%E9%94%81%E6%A8%A1%E5%BC%8F%E4%B8%BA%E4%BB%80%E4%B9%88%E5%BF%85%E9%A1%BB%E5%8A%A0%20volatile%EF%BC%9F.md
 *
 * https://www.cnblogs.com/xz816111/p/8470048.html
 */