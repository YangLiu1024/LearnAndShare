# Annotation
Java 提供了一些内置的 注解，也支持用户自定义注解. 注解一般用来在 compiler 阶段使用，也可以 applied 到 java doc 里。  
注解可以有多个参数，如果只有一个参数，且参数名是 value, 则在使用的时候，可以不写参数名。如果注解没有参数，那么可以不写括号  
注解可以给 class 声明，field, method 等使用。
```java
// 定义了一个注解
@interface ClassPreamble {
   String author();
   String date();
   int currentRevision() default 1; // 可以指定默认值
   String lastModified() default "N/A";
   String lastModifiedBy() default "N/A";
   // Note use of array
   String[] reviewers();
}

// 使用这个注解
@ClassPreamble (
   author = "John Doe",
   date = "3/17/2002",
   currentRevision = 6,
   lastModified = "4/12/2004",
   lastModifiedBy = "Jane Doe",
   // Note array notation
   reviewers = {"Alice", "Bob", "Cindy"}
)
```
# meta-annotation
有一些注解，是给其它注解使用的注解，称之为 元注解。
```java
// java.lang.annotation 提供了一些 元注解
@Retention // 表明被修饰的注解该怎么存储
RetentionPolicy.SOURCE // 只在 source level, compiler 会丢弃该注解
RetentionPolicy.CLASS // compiler 在编译阶段会保留该注解，但是 JVM 不会处理该注解
RetentionPolicy.RUNTIME // JVM 会保留该注解，所有该注解可以在 runtime 被使用

@Documented // 表明该注解修饰的注解，需要被 javadoc tool documented

@Target // 该注解用来表明被修饰的 注解能够被 applied 到什么 Java 元素上

ElementType.ANNOTATION_TYPE // 能够被用给其它注解
ElementType.CONSTRUCTOR
ElementType.FIELD
ElementType.LOCAL_VARIABLE
ElementType.METHOD
ElementType.PACKAGE
ElementType.PARAMTER // 方法的参数
ElementType.TYPE // any element of a class

```