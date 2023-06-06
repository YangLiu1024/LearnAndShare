# SpringBoot
Spring boot 是一个快速开发部署框架，其核心特性
* 依赖注入
* 面向切面
* 自动配置
* 独立部署

SpringBoot 是一个快速开发框架，在其之上集成了许多组件，可以开发各种应用，最常见的就是 web 服务。
```xml
// 创建一个 maven 工程，添加以下依赖
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.1.7.RELEASE</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```
定义一个 `Application` 作为应用的入口
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
添加一个 rest service handler
```java
@RestController
public class WebHandler {
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World";
    }
}
```
运行 Application，可以看到 web service 就起起来了。访问 `http://localhost:8080/hello` 会返回 *Hello World*