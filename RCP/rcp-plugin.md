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
'Overview' 页面包含了多个配置区域， 'General Information' 页面显示常规信息。

'Dependencies' 页面显示当前插件对其它插件的依赖，在此页中，必须列出所有该插件项目所需要的第三方插件或插件的包路径。'Required plug-ins' 列表中插件的顺序也是有意义的，此顺序决定了类在运行期间的加载顺序。

'Runtime' 页面包含 'Export Package', 'Package visibility' 和 'Classpath' 三个配置项。 其中 'Export Package' 和 'Package visibility' 是紧密关联的， 'Package visibility' 规定了package 在导出后在插件之间的可见性。 Eclipse 将 package 可见性分为四类： Accessible, Forbidden, Internal, Internal with friends. 'Classpath' 用来添加第三方 jar 文件到 classpath，一般不建议使用.

'Extensions' 用来扩展其它插件提供的扩展点，以达到只需要通过配置，不需要写代码，就实现所需功能的目的。如果使用了某个插件提供的扩展点，那么就需要在 'Dependencies' 中添加该插件作为依赖。

'Extension Points' 用来定义插件自己的扩展点，需要创建一个扩展点的配置文件，并且编写程序来加载创建的扩展点和解析扩展点中的配置信息，编写业务逻辑程序处理这些配置。

'Build' 页面中， 'Runtime Information' 列出所有要构建的库以及它关联的源文件目录。 'Binary Build' 用来选择需要打包到插件中的所有文件和文件夹， 'Source Build'一般不使用。

'MANIFEST.MF', 'plugin.xml' 和 'build.properties' 几个文本编辑页面一般不需要手动修改，它们会通过前面的页面内容自动同步更新

### RCP Avtivator 和 Advisor 类解析
当我们 create project based on templates, 会自动生成一些 Java 类，这几个类在 RCP 程序中起到了非常重要的作用。
#### Activator
Activator 类是在创建 RCP 程序向导中创建的，用来控制插件的生命周期，
* 如果该插件面向的是 OSGi 框架，那么Activator 类将继承 `org.osgi.framework.BundleActivator` 接口
* 如果该插件是non-ui 插件，那么 Activator 将继承 `org.eclipse.core.runtime.Plugin` 类
* 如果该插件是 UI 插件(含有 SWT, JFace 等)，那么Activator 类将继承 `org.eclipse.ui.plugin.AbstractUIPlugin`

其中最重要的两个接口，是定义在 BundleActivator中的 `start(BundleContext context)` 和 `stop(BundleContent context)`，将在插件启动，关闭的时候调用。

一个插件只能在 Manifest 中定义一个 BundleActivator, Fragment 类型的插件不需要拥有 BundleActivator.

#### Application
Application 实现 `org.eclipse.equinox.app.IApplication` 接口，该类是 RCP 程序的主要入口。虽然这个类提供了启动和停止应用的两个 public 接口， 但是不需要自己来调用启动和停止操作，框架平台会调用它们。 并且，需要扩展 `org.eclipse.core.runtime.applications` 扩展点来定义一个 application，在该扩展点的 class setting 中，指定创建的实现了 `org.eclipse.equinox.app.IApplication` 的类。 该 application 扩展点会在 product 扩展点配置中使用，将 application 扩展点和 product 扩展点绑定在一起。

#### WorkbenchAdvisor
用于配置工作台的类，执行一些初始化的工作， 并且 WorkbenchAdvisor 在创建工作台之前创建并完成初始化，一般在 Application start 方法中创建。

#### WorkbenchWindowAdvisor
WorkbenchAdvisor 中 createWorkbenchWindowAdvisor 接口需要返回一个 WorkbenchWindowAdvisor实现类，在该实现类中初始化 window 相关的一些配置，以及创建 ActionBarAdvisor

#### ActionBarAdvisor
ActionBarAdvisor 用来负责应用程序的顶级菜单，工具栏和状态行的显示以及 Action的创建。
```java
protected void makeActions(IWorkbenchWindow window)//used to create actions, and use register(Action) to regist the actions
protected void fillMenuBar(IMenuManager menuBar)//used to populate menu bar
protected void fillCoolBar(ICoolBarMenuManager coolBar)//used to populate toolbar
protected void fillStatusLine(IStatusLineManager statusLine)//used to populate status line
```
