# Bean 的注入
 SpringBoot 看起来只是使用了一些注解，没有任何耦合，就完成了所有的事情，其底层是怎么实现的呢？答案就在注解里。  
 SpringBoot 有两种方法来发现 Bean
 * @ComponentScan -> 自动扫描
 * @Import -> 手动指定注入

看似我们没有使用 @ComponentScan， 但是其实 @SpringBootApplication 已经包含了
```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {

}
```
`@ComponentScan` 有几个关键的参数
* basePackage -> SpringBoot 开始自动扫描的 base package, SpringBoot 会自动扫描该 package 以及它之下的所有 sub package, 如果不指定，则默认是 SpringBootApplication 所在的 package
* basePackageClasses
* includeFilters -> 配置过滤器
* excludeFilters

如果有一些 Bean 在 SpringBootApplication 的 package 之外，一个办法是设置 basePackage, 让它能够 cover 你所有想要解析的代码。  
另一个办法就是使用 `@Import`, 该注解可以让你只导入指定 Bean, 而不是整个 package
```java
// package spring.boot.outside
@Component
public class Outside {

}

// package spring.boot.root
@SpringBootApplication
@Import(Outside.class)
public class Application {
    @Autowired
    private Outside outside
}
```

