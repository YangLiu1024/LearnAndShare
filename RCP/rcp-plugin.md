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

### RCP Product Export
基于 Eclipse 的产品是使用 Eclipse 平台构建的独立程序。产品可以作为一个或多个插件打包，交付。PDE(plugin development environment) 使用产品配置文件来管理产品的配置信息，包括产品的启动页面，图标显示等。 Eclipse 提供产品的导出功能，使 RCP 产品能够脱离 Eclipse 开发环境独立运行。可以基于当前已有的 product 创建一个产品配置文件，该文件需要以 `.project` 结尾。

#### Product configuration file
产品编辑页面与插件清单编辑页面类似，有多个配置页。
 - `Overview` 页面定义了产品的基本信息, 其中 `the product includes native launcher artifacts` 表示导出后创建可执行文件
 - `Contents` 页面展示了当前产品依赖的所有其它插件，可以使用'Add required Plug-ins' 来确保没有遗失依赖项
 - `Configuration` 定义产品运行时需要的配置文件，这个文件一般会在 ${product_folder}/configurations/config.ini. 配置文件里一般会指定 osgi.bundles(所有的依赖项), osgi.bundles.defaultStartLevel(插件启动时采用的默认启动级别，默认为4). osgi.product(指定要启动的产品的标识符), osgi.splashPath(启动页面的路径)。除了配置文件，还需要配置插件的 'Start Level',比如有的插件必须优先自动启动，那么就要在这里配置好。
 - `Launching` 指定运行时执行环境，启动参数等
 - `Splash` 设置产品启动页面图片，名为 splash.bmp, 需要放在插件的根目录下
 #### Synchronize function
 在 `Overview` 页面有一个`Synchronize` 功能。需要知道，产品配置文件只是一个辅助功能，用于设置产品相关配置。而应用程序真正运行时并不是依靠读取这个产品配置文件来执行的，需要将产品配置文件中的信息同步到插件的 `plugin.xml` 中，这就是同步操作的作用，它确保产品配置文件中的内容和插件 `plugin.xml` 同步，产品配置文件的保存功能不会触发同步。
 #### Launch product and Launch application
 插件页面和产品配置页面都提供了运行链接，但是它们还是有一些区别的。在插件概述页面的运行称为 application 运行，在产品概述页面的运行称为 product 运行。应用运行决定了开发阶段应用程序的运行状况，产品运行决定了产品最终导出后在实际环境中的运行状况。
 #### Common Error for export
  - `product can not been found` => 查看产品是否包含所有依赖项
  - `splash or other icons are missing` => 查看 build.properties 里是否包含了所有需要的文件
  - `service could not been found or injected` => 确保提供 service 的 插件启动了'Activate the plug-in when one of its class is loaded', 然后确保 org.apache.felix.src 应该 auto start, 并且 start-level < 4
  - `application id could not been found` => org.eclipse.core.runtime 插件要auto start， 并且 start-level 要为 1
 
 ## RCP Extensions
 通过对 Extension 的使用，可以快速的完成功能开发。
 ### Product 扩展
 为了定义一个 product,需要扩展 org.eclipse.core.runtime.products, 这个扩展可以定义一个 `product`，product 需具有 id 以及其它 property
 
 这儿的 id 为 'product', 最后完整的 product id 为 plugin id + id here. for example, the plugin id is `com.rcp.plugin.app`, 那么对应的 product id 就是 `com.rcp.plugin.app.product`
 ```xml
   <extension
         id="product"
         point="org.eclipse.core.runtime.products">
      <product
            name="%product.name"
            application="com.rcp.plugin.app.application">
         <property
               name="windowImages"
               value="icons/eclipse16.png,icons/eclipse32.png,icons/eclipse48.png,icons/eclipse64.png, icons/eclipse128.png,icons/eclipse256.png">
         </property>
         <property
               name="appName"
               value="%product.name">
         </property>
      </product>
   </extension>
```
### Application 扩展
每个 product 需要指定一个 Application，为了定义 Application，需要扩展 org.eclupse.core.runtime.applications
```xml
   <extension
         id="application"
         point="org.eclipse.core.runtime.applications">
      <application>
         <run
               class="com.rcp.plugin.app.intro.Application">
         </run>
      </application>
   </extension>
```
其中 class 指定了具体的实现了 org.eclipse.equinox.app.IApplication 的类，这个类是整个 application的入口类，类似于 Java 程序的 main 方法。

与 product id 类似，这儿的 id 为 'application', 最后的 application id 为 `com.rcp.plugin.app.application`, 并且在 product 扩展中被使用。
### Commands 扩展
Commands 扩展用来定义各种 command, 每个command 还可以包含多个 commandParameter. 这儿的 command 可以被 menu item 或者 toolbar item 引用. 同样的，每个 command 需要有自己 unique id.
```xml
   <extension
         point="org.eclipse.ui.commands">
      <command
            id="com.rcp.plugin.app.command.language"
            name="Language">
         <commandParameter
               id="com.rcp.plugin.app.language.locale"
               name="locale"
               optional="true">
         </commandParameter>
      </command>
   </extension>
```
这里定义了一个 'com.rcp.plugin.app.command.language' command, 且这个 command 拥有一个 parameter， 其 id 为 'com.rcp.plugin.app.language.locale'。

Eclipse 提供了很多内置的 action 以及对应的 command id, 可以参考 class ActionFactory
### Menus 扩展
Menus 扩展用来定义各种菜单栏，工具栏等。它由元素 `menuContribution` 组成，每一个 menuContribution 元素需要指定 'locationURI', 它标志着该 menuContribution 元素对应的内容需要被添加到什么位置。它的格式为 `menu-schema:menu-id?<placement>=<menu-item-id>`, 更详细点， 就是 `[menu|toolbar|popup]:[existing menu id: existing view id]?[before|after|endof]=[the existing menu item id]`。menu-schema 是用来指定要找哪种类型的 menu, 后面跟着指定的 menu 的 id. placement 是指需要放在指定 menu-item 的相对位置。placement 并不能完全保证相对位置，它更多的取决于插件的加载顺序。

Each 'menuContribution' 可以包含多个 'command', 'dynamic', 'menu', 'toolbar', 'separator' 元素
```xml
<extension
         point="org.eclipse.ui.menus">
      <menuContribution
            allPopups="false"
            locationURI="menu:help?after=additions">
         <menu
               id="com.rcp.plugin.app.menu.language"
               label="Language"
               mnemonic="L">
            <command
                  commandId="com.rcp.plugin.app.command.language"
                  id="com.rcp.plugin.app.language.zh"
                  label="Chinese"
                  style="push">
               <parameter
                     name="com.rcp.plugin.app.language.locale"
                     value="zh_CN">
               </parameter>
            </command>
            <command
                  commandId="com.rcp.plugin.app.command.language"
                  id="com.rcp.plugin.app.language.en"
                  label="English"
                  style="push">
               <parameter
                     name="com.rcp.plugin.app.language.locale"
                     value="en">
               </parameter>
            </command>
         </menu>
      </menuContribution>
      <menuContribution
            allPopups="false"
            locationURI="toolbar:org.eclipse.ui.main.toolbar?after=additions">
         <toolbar
               id="com.rcp.plugin.app.toolbar.save.actions">
            <command
                  commandId="org.eclipse.ui.file.save"
                  disabledIcon="icons/disable-save-24.png"
                  icon="icons/save-16.png"
                  label="Save"
                  style="push"
                  tooltip="Save">
            </command>
            <command
                  commandId="org.eclipse.ui.file.saveAll"
                  disabledIcon="icons/disabled-save-all-16.png"
                  icon="icons/save-all-16.png"
                  label="SaveAll"
                  style="push">
            </command>
         </toolbar>
      </menuContribution>
   </extension>
```
for example, 第一个 menuContribution 的 locationURI 是 'menu:help?after=additions' 表示要去找 help menu, 它的 id 是 help,它是在我们 ApplicationActionBarAdvisor 里注册的
```java
	@Override
  protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_HELP, IWorkbenchActionConstants.M_HELP);
		menuBar.add(helpMenu);
	}
```
然后 'additions' 是一个特殊的 id, 表示 'other menu item', 这个 URI 的意思就是将去查找 id 为 help 的 menu，将自己 menuContribution 的元素添加到这个 menu 其它已有的 item 之前。

之后，在该 menuContribution 元素里添加一个 menu 元素，该元素有自己的 id, 可以被用在其它的 locationURI里。 在 menu 元素里， 添加了两个command 元素。 每个command 元素表示一个 menu item, 每一个 menu item 需要和一个 command id绑定，表示当用户点击该 menu item 时，触发该 command id. 当该 command id 需要参数时，可以在该 command 元素里添加 parameter 元素，该元素指定一个 commandParameter id 和一个对应的 value. 这样，当触发该 menu item 对应的 command id 时，会将该 parameter value 传递出去，然后在该 command id 对应的 handler 里面，可以读取该参数并继续处理。
```java
public class SwitchLanguageHandler extends AbstractHandler {
   public Object execute(ExecutionEvent event) throws ExecutionException {
    System.out.print(event.getParameter("com.rcp.plugin.app.language.locale"));
   }
}
```
第二个 locationURI 是 'toolbar:org.eclipse.ui.main.toolbar?after=additions', 这里的 'org.eclipse.ui.main.toolbar' 是内置的 toolbar id， 表示 main toolbar. 还有 'org.eclipse.ui.main.menu', 表示 main menu. 之后，在 main toolbar 里面添加了一个 toolbar, 这个 toolbar 里面添加了两个 command 元素，这两个 command 也和内置的 command id 'org.eclipse.ui.file.save' 和 'org.eclipse.ui.file.saveAll' 绑定在一起。这两个 command id 会在视图触发 p.firePropertyChange(ISaveablePart.PROP_DIRTY)) 事件时更新状态。

