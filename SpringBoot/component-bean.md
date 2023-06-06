# 容器和 Bean
Java 代码里，一个类通常依赖于其它的类，那么该类就需要维护依赖项的构建与注入。这在大部分时候都是一个繁琐的工作。  
SprintBoot 容器的概念，其实就是依赖注入和控制反转的实现。
* 控制反转 -> 把一个类的依赖项的构建的职责往上移
* 依赖注入 -> 把一个类的依赖项注入

SpringBoot 就是用一个大大的容器，放入我们所有的 Bean, 我们需要做的就是
* 把 bean 放入容器
* 从容器取出对象

# 创建 Bean
SpringBoot 提供两种方式创建 Bean， Bean 默认都是单例的。
* @Component -> 修饰实体类，非接口或抽象类。被修饰的类会被注册为 Bean, 放入容器。 Component 只有一个参数 value, 用以定义 Bean 的名字，如果不指定，则 Bean 名默认为类名
* @Bean -> 用来修饰方法，方法的返回值即为要注册的 Bean, 如果不指定名字，方法的名字则是 Bean 的名字。 需要注意的是，@Bean 只能在已经声明为 Bean 的 class 里使用, 且修饰的方法的参数都是默认自动注入的

# 取出 Bean
* @Autowired -> 修饰 field 或者方法，修饰的field 会自动注入，修饰方法的话，方法的参数会自动注入

Autowired 有一个参数 `required`， 默认值为 true, 表示如果容器内没有满足条件的 Bean, 则容器会初始化失败。 
注入默认通过类型识别
* 普通 bean -> 寻找类型匹配的 Bean
* Optional<T> -> 寻找类型匹配的 T，然后包装为 Optional, 即使不存在，也不会报错，返回 Optional.empty, 相当于 @Autowired(required = false)
* List<T -> 寻找所有类型匹配的 Bean
* Map<String, T> -> 寻找所有类型匹配的 bean， 并通过 Bean 的名字映射 
* javax.inject.Provider<T> -> 提供一个可以获取 T 的 Bean

再使用 Bean 的时候，如果 Bean 的实例有多个，但是方法参数又只需要一个，需要使用 @Qualifier 来指定需要注入的 bean
```java
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public String world() {
        return "world";
    }

    @Bean("beautiful")
    public String beauty() {
        return "beauty";
    }

    @Autowired
    @Qualifier("beautiful")
    // 有多个 String 的 bean 对象，需要使用 Qualifier 指定使用的 Bean, 如果不指定，容器初始化会出错
    public void hello(String who) {
        System.out.println("Hello " + who);
    }
}
```
