# 元注解
SpringBoot 大量使用注解来简化配置，但是存在很多重复的 注解配置怎么办？SpringBoot 提供了新的元注解来简化注解配置

```java
//AnnoA 和 AnnoB 在大量地方重复使用，如果每个地方都重复书写，则很难维护
@AnnoA("paramA")
@AnnoB(ParamB.class)
class A {
    
}

@AnnoA("paramA")
@AnnoB(ParamB.class)
class B {
    
}

//SpringBoot 提供了元注解的机制, MyCOnfig 就是一系列注解配置的集合
@AnnoA("paramA")
@AnnoB(ParamB.class)
@interface MyConfig {
    
}

@MyConfig
class A {
}

@MyConfig
class B {
}
```
但有的时候，一些注解虽然相似，但是在不同场景下会有些许差别，这样我们就没办法单纯的组合所有注解。SpringBoot 提供了新的 `@AliasFor` 注解来解决这个问题
```java
//AnnoC 在不同场景下会有不同的参数
@AnnoA("paramA")
@AnnoB(ParamB.class)
@AnnoC(param = "ConfigForA")
class A {
    
}

@AnnoA("paramA")
@AnnoB(ParamB.class)
@AnnoC(param = "ConfigForB")
class B {
    
}
// 为了解决这个问题，使用 AliasFor 注解， AliasFor 是给 METHOD 使用的注解
@AnnoA("paramA")
@AnnoB(ParamB.class)
@AnnoC
@interface MyConfig {
    // 如果不使用 AliasFor, MyConfig 的参数会作用于 MyConfig
    // 这里使用了之后，表示 MyConfig 接收的 value 参数其实是 AnnoC 注解 param 属性的别名
    @AliasFor(annotation = AnnoC.class, attribute = "param") 
    String value() default "";
}

@MyConfig("ConfigForA")
class A {
}

@MyConfig("ConfigForB")
class B {
}
```
比较有趣的是，我们可以看一下 AliasFor 的实现
```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AliasFor {
    // AliasFor 注解有两个属性 value 和 attribute, 且互为别名
    // 为什么这样写呢？ 这是因为，如果该注解只有一个参数，那么可以直接简写 @Anno("abc"), 而不用写 @Anno(attribute = "abc")
    // 当有多个参数时，如果写 @Anno(value="abc", filter = "select * from table"), value 参数名本身不具备什么信息，不如 @Anno(attribute="abc", filter="")
    @AliasFor("attribute")
    String value() default ""; 

    @AliasFor("value")
    String attribute() default "";

    Class<? extends Annotation> annotation() default Annotation.class;
}
```