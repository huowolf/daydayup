# Spring MVC工作流程

![img](https://upload-images.jianshu.io/upload_images/5763525-809bdd7c823629eb.png?imageMogr2/auto-orient/strip|imageView2/2/w/708/format/webp)

## 名词解释：

* DispatcherServlet：前端控制器，是 SpringMVC 工作流程的中心，负责调用其他组件，在系统启动时就加载该类。
* Handler：后端处理器，对用户具体请求进行处理，也就是我们编写的 Controller 类。
* HandlerMapping：处理器映射器，根据用户访问的 URL 映射到对应的后端处理器 Handler，根据不同的映射处理器可实现不同的映射，比如 xml 配置（现在不常用）、注解配置（最常用）。
* HandlerExecutionChain：后端处理器 Handler 相关对象，包括 Handler 对象和对应的拦截器对象，以 HandlerExecutionChain 对象包含了这些相关的对象。
* ModelAndView：逻辑视图，包括数据模型和视图名。
* HandlerAdapter：处理器适配器，调用后端处理器中的方法，返回逻辑视图 ModelAndView 对象。
* ViewResolver：视图解析器，将 ModelAndView 逻辑视图解析为具体的视图（如 JSP，PDF等）。

## 工作流程

1、当用户向服务器发送请求时，会被 DispatcherServlet 拦截。

2、DispatherServlet 解析用户访问的 URL，并调用处理器映射器 HandlerMapping。

3、处理器映射器 HandlerMapping 映射到对应的后端处理器 Handler（注意这里只是找到了对应的 Controller 类，并没有执行其中的方法），Handler 对象以及 Handler 对象相关的拦截器对象会被封装到 HandlerExecutionChain 对象中返回给 DispatcherServlet。

4、DispatcherServlet 根据后端处理器 Handler 对象来调用适合的处理器适配器。

5、HandlerAdapter 调用 Handler 对象执行 Handler 中的方法，在 Handler 的方法中，可以做一些额外的工作，如消息转换（如 JSON、XML 和 Java 对象的互转）、数据转换（如 String 和 Integer、Double的互转）、数据格式化（如日期）、数据校验（后端校验），最终返回 ModelAndView 对象给 DispatcherServlet，该对象包含视图名和数据模型。

6、DispatcherServlet 根据 ModelAndView 对象来调用适合的视图解析器 ViewResolver。

7、ViewResolver 解析 Model 和 View 返回具体的 view 给 DispatcherServlet。

8、DispatcherServlet 对 view 进行渲染，返回具体的视图给客户端显示，如 JSP，JSON、XML、PDF等。







