# SPI机制

SPI 全称为 Service Provider Interface，是一种服务发现机制。SPI 的本质是将接口实现类的全限定名配置在文件中，并由服务加载器读取配置文件，加载实现类。这样可以在运行时，动态为接口替换实现类。正因此特性，我们可以很容易的通过 SPI 机制为我们的程序提供拓展功能。

## JDK SPI

JDK 中 提供了一个 SPI 的功能，核心类是 java.util.ServiceLoader。其作用就是，可以通过类名获取在"META-INF/services/"下的多个配置实现文件。

为了解决上面的扩展问题，现在我们在META-INF/services/下创建一个com.github.kongwu.spisamples.SuperLoggerConfiguration文件（没有后缀，接口的全限定名）。文件中只有一行代码，那就是我们默认的com.github.kongwu.spisamples.XMLConfiguration（注意，一个文件里也可以写多个实现，回车分隔）

#### 劣势

JDK SPI机制的一个劣势，无法确认具体加载哪一个实现，也无法加载某个指定的实现，仅靠ClassPath的顺序是一个非常不严谨的方式

## Dubbo SPI

Dubbo 中实现了一套新的 SPI 机制，功能更强大，也更复杂一些。相关逻辑被封装在了 ExtensionLoader 类中，通过 ExtensionLoader，我们可以加载指定的实现类。Dubbo SPI 所需的配置文件需放置在 META-INF/dubbo 路径下，配置内容如下。

```properties
optimusPrime = org.apache.spi.OptimusPrime
bumblebee = org.apache.spi.Bumblebee
```

与 Java SPI 实现类配置不同，Dubbo SPI 是通过键值对的方式进行配置，这样我们可以按需加载指定的实现类。另外在使用时还需要在接口上标注 @SPI 注解。

Dubbo SPI 和 JDK SPI 最大的区别就在于支持“别名”，可以通过某个扩展点的别名来获取固定的扩展点。就像上面的例子中，我可以获取 Robot 多个 SPI 实现中别名为“optimusPrime”的实现，也可以获取别名为“bumblebee”的实现，这个功能非常有用！

## Spring SPI

Spring 的 SPI 配置文件是一个固定的文件 - META-INF/spring.factories，功能上和 JDK 的类似，每个接口可以有多个扩展实现，使用起来非常简单：

```java
//获取所有factories文件中配置的LoggingSystemFactory
List<LoggingSystemFactory>> factories = 
    SpringFactoriesLoader.loadFactories(LoggingSystemFactory.class, classLoader);
```

Spring SPI 中，将所有的配置放到一个固定的文件中，省去了配置一大堆文件的麻烦。至于多个接口的扩展配置，是用一个文件好，还是每个单独一个文件好这个，这个问题就见仁见智了。

如果我们要扩展某个接口的话，只需要在你的项目（spring boot）里新建一个META-INF/spring.factories文件，只添加你要的那个配置，不要完整的复制一遍 Spring Boot 的 spring.factories 文件然后修改。

## 参考

https://segmentfault.com/a/1190000039812642

https://dubbo.apache.org/zh/docs/v2.7/dev/source/dubbo-spi/

