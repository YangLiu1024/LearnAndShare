# Introduction to Eclipse RCP - OSGi

OSGi 是基于 Java 的服务平台规范, OSGi 的本质是将 Java 面向对象的开发转向面向组件和服务的开发。 OSGi 框架提供了一套完善的机制用于管理和控制组件和服务的生命周期, 以及组件和服务在其生命周期内的交互. 

Equinox 项目是 OSGi 核心框架规范的一个实现, 并且作为 Eclipse 默认动态基础架构, 为Eclipse 的其它模块所使用

## Eclipse 概念

基于 Eclipse 开发的 RCP 应用程序会和 Eclipse 拥有相似的外观，大致由 工作台，菜单栏，工具栏，编辑区，视图等构成。

### 透视图 - Perspective
每个工作台都包含一个或多个透视图，透视图定义了工作台中一组透视图和编辑区的初始布局，并且每个透视图共享同一组编辑器。透视图是为了某一特定认为组合在一起的多个视图和编辑区。在开发的应用程序中，可以将一组业务相关的视图放置在同一视图下

### 视图 - View
视图区域(ViewPart) 是透视图中最常见的一种容器， 视图具有自己的工具栏和视图下拉菜单，工具栏和下拉菜单中的选项可以根据所选择的对象动态的发生改变。 视图可以单独出现，也可也与其它视图一起堆叠放在选项卡式容器中。在工作台中，可以打开，关闭，拖拽视图来更改视图的显示。

### 编辑器 - Editor
工作台中的大部分透视图一般都包含一个编辑器区域(EditorPart), 在编辑器区域可以将不同的编辑器与不同类型的文件或者对象相关联。编辑器堆叠放在编辑器区域中，可同时打开多个编辑器，但任意时刻只能有一个编辑器处于活跃状态

## Eclipse RCP 
Eclipse RCP 开发工作流程的第一步就是创建一个插件项目。
### Plug-in Project Basic Setting
`project id` 是该plug-in project 的唯一标识符, `Rich Client Application` 表示当前 project 是否为一个独立的应用程序而不只是一个普通的插件. Activator 是控制插件生命周期的 Java 类， 当需要在插件启动和关闭时执行初始化和释放资源操作的时候，才需要使用 Activator

version 需要符合 Eclipse 插件版本的格式: `major.minor.service.qualifier`, 前面三个部分都是整数，最后一个是字符串，其加载顺序优先级由高到低。比如项目名称 `org.eclipse.core.runtime_3.7.0.v20110110`, 由项目ID, 下划线和版本号组成。

当产品的 API 发生重大变化时，major 部分应该增加，且将 minor 和 service 置为 0.

当 API 的改动能被外部察觉，minor 部分需增加。

当不同发布版本之间的插件发生改变，比如代码中的 bug 被修复，编译选项设置改变等，增加 service 部分

qualifer 用于描述不同 build 之间的变化.

### 插件清单编辑器 - plugin.xml
