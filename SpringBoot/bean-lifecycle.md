# Bean 的配置
## Scope
SpringBoot 里所有的 Bean 默认都是 singleton, 如果想要非单例的 Bean, 需要定义 `@Scope`.  
SpringBoot 提供了两种 scope, singleton  和 prototype, Spring-web 里还定义了 request 和 session 等 scope
* singleton, 一个容器里只有一个 Bean, 不同的依赖者使用的是相同的依赖
* prototype, 不同的依赖者使用不同的 Bean, 即每一次获取 Bean 都会返回新的 Bean
* request, 每一个 http request 都会创建一个新的 Bean
* session, 每一个 http session 都会创建一个新的 Bean

## Conditional
一些 Bean 的注入是依赖于环境的。比如我们需要在环境里注入一个 Driver, 当 classpath 里存在 Mysql connector 时注入 MysqlDriver, 当存在 Sqlite connector 时，注入 SqliteDriver  
这个时候就需要使用 `@Conditional`, 该注解是一个元注解，SpringBoot 定义了一些注解可供使用，常用的有
* ConditionalOnClass, 当类存在时，创建 Bean
* ConditionalOnMissingClass, 当类不存在时，创建Bean
* ConditionalOnBean, 当 Bean 存在时，创建 bean
* ConditionalOnMissingBean, 当 bean 不存在时，创建 bean

## DependsOn
除了环境依赖，一些 Bean 还存在顺序依赖，可以使用 `@DependsOn` 来指明当前 Bean 依赖于其它 bean
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @DependsOn("beanA2") // try comment out this line. Bean 的加载顺序会改变
    public static BeanA beanA1() {
        System.out.println("Bean 1");
        return new BeanA(1);
    }

    @Bean
    public static BeanA beanA2() {
        System.out.println("Bean 2");
        return new BeanA(2);
    }
}
```

## Lazy
设置 Bean 为懒加载模式，在容器初始化时，并不会主动构建 Bean, 只有在第一次被依赖时构造
## Primary
设置 Bean 为首选，如果容器内有多个相同类型的 Bean, primary bean 会被优先使用
## Profile
是一种 conditional，只有在某种 profile 下面 Bean 才会被创建