## RabbitMQ核心概念

RabbitMQ整体架构:

![img](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-16/96388546.jpg)

在RabbitMQ中，消息并不是直接被投递到Queue（消息队列）中，中间还必须经过Exchange （交换器）这一层，Exchange(交换器)会把我们的消息分配到Queue（消息队列）中。

Exchange用来接收生产者发送的消息并将这些消息路由到服务器中的队列中，如果路由不到，或许会返回给Producer（生产者），或许会被直接丢弃掉。

RabbitMQ的Exchange（交换器）有4种类型，不同的类型对应着不同的路由策略：direct（默认）、fanout、topic 和 headers，不同类型的Exchange转发消息的策略有所区别。

Exchange（交换器）示意图：

![img](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-16/24007899.jpg)

生产者将消息转发给交换器的时候，一般会指定一个RoutingKey（路由键），用来指定这个消息的路由规则，而这个RoutingKey需要与交换器类型和绑定建联合使用才能最终生效。

RabbitMQ通过Binding（绑定）将Exchange（交换器）与Queue（消息队列）关联起来，在绑定的时候一般会指定一个BindingKey（绑定键），这样RabbitMQ就知道如何正确将消息路由到队列了。

Binding（绑定）示意图：

![img](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/18-12-16/70553134.jpg)

生产者将消息发送给交换器时，需要一个RoutingKey,当 BindingKey 和 RoutingKey 相匹配时，消息会被路由到对应的队列中。在绑定多个队列到同一个交换器的时候，这些绑定允许使用相同的 BindingKey。BindingKey 并不是在所有的情况下都生效，它依赖于交换器类型，比如fanout类型的交换器就会无视，而是将消息路由到所有绑定到该交换器的队列中。 



## 参考

https://javaguide.cn/high-performance/message-queue/rabbitmq-intro/