# Spring bean的生命周期

首先，普通Java对象和Spring所管理的Bean实例化过程是有些区别的。

普通的Java环境下创建对象简要的步骤，可以分为以下几步：

1、java源码被编译为被编译为class文件

2、等到类需要被初始化时（比如说new、反射等）

3、class文件被虚拟机通过类加载器加载到JVM

4、初始化对象供我们使用

简单来说，可以理解为它是用Class对象作为模板进而创建出具体的实例。

而Spring所管理的Bean不同的是，除了Class对象之外，还会使用BeanDefinition实例来描述对象的信息。

比如说，我们可以在Spring所管理的Bean有一系列的描述：@Scope、@Lazy、@DependsOn等。

可以理解为：Class只描述了类的信息，而BeanDefinition描述了对象的信息。

Spring在启动的时候需要扫描在XML/注解/JavaConfig中需要被Spring管理的Bean信息。随后，会将这些信息封装为BeanDefinition，最后会把这些信息放到一个beanDefinitionMap中。

这个Map的keys是beanName，value则是BeanDefinition对象。

到这里其实就是把定义的元数据加载起来，目前真实对象还没实例化。

接着会遍历这个beanDefinitionMap，执行BeanFactoryPostProcessor这个Bean后置处理器的逻辑。

比方说，我们平时定义的占位符信息，就是通过BeanFactoryPostProcessor的子类PropertyPlaceholderConfigurer进行注入进去。

当然，这里我们也可以自定义BeanFactoryPostProcessor来对我们定义好的Bean元数据进行获取或者修改。只是一般我们不会这样干，实际上也很少有使用场景。

BeanFactoryPostProcessor后置处理器执行完了以后，就到了实例化对象啦。

在Spring里边是通过反射来实现的，一般情况下会通过反射选择合适的构造器来把对象实例化。

但这里把对象实例化，只是把对象给创建出来，而对象具体的属性还没注入的。

比如我的对象是UserService，而UserService对象依赖着SendService对象，这时候的SendService还是null的。

所以，下一步就是把对象的相关属性给注入。

相关属性注入完以后，往下就是初始化的工作了。

首先判断该bean是否实现了Aware相关的接口，如果存在则填充相关的资源。

比如我这边在项目中用到的：我希望代码程序的方式去获取指定的Spring Bean，我们会抽取成一个工具类，去实现ApplicationContextAware接口，来获取ApplicationContext对象进而获取Spring Bean。

Aware相关的接口处理完之后，就会到BeanPostProcessor后置处理器啦。

BeanPostProcessor后置处理器有两个方法，一个是before，一个是after。那肯定是before先执行，after后执行。这个BeanPostProcessor后置处理器是AOP实现的关键。

所以，执行完Aware相关的接口都会执行BeanPostProcessor相关子类的before方法。

BeanPostProcessor相关子类的before方法执行完，则执行init相关的方法，比如说@PostConstruct、实现了InitializingBean接口、定义的init-method方法。

当时我还去官网去看他们被调用【执行顺序】分别是：@PostConstruct、实现了InitializingBean接口以及init-method方法。

这些都是Spring给我们的拓展，像@PostConstruct我就经常用到。

等到init方法执行完之后，就会执行BeanPostProcessor的after方法。

基本重要的流程已经走完了，我们就可以获取到对象去使用了。

销毁的时候就看有没有配置相关的destroy方法，执行就完事了。

![img](https://gitee.com/huowolf/pic-md/raw/master/7a1af541cee944c4a59a8231f5c07731~tplv-k3u1fbpfcp-watermark.awebp)

