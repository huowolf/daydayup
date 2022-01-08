## springboot与docker

## 镜像的分层构建

Dockerfile文件：

```bash
FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

EXPOSE 9999
```

## 打包时自动构建镜像

pom.xml文件中增加dockerfile-maven-plugin插件

```bash
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.4.13</version>
                <executions>
                    <execution>
                        <id>default</id>
                        <goals>
                            <goal>build</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <repository>huowolf/${project.artifactId}</repository>
                    <tag>${project.version}</tag>
                </configuration>
            </plugin>
```

## 说明

本示例的镜像已经推送到dockerhub下，地址：

https://hub.docker.com/r/huowolf/springboot-demo

拉取该镜像命令为：

```bash
docker pull huowolf/springboot-demo
```

## 参考

1、https://docs.spring.io/spring-boot/docs/2.6.1/reference/htmlsingle/#container-images.dockerfiles

2、https://github.com/spotify/dockerfile-maven

