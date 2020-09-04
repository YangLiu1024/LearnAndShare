# Introduction to Eclipse RCP

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
第二个 locationURI 是 'toolbar:org.eclipse.ui.main.toolbar?after=additions', 这里的 'org.eclipse.ui.main.toolbar' 是内置的 toolbar id， 表示 main toolbar. 还有 'org.eclipse.ui.main.menu', 表示 main menu. 

之后，在 main toolbar 里面添加了一个 toolbar, 这个 toolbar 里面添加了两个 command 元素，这两个 command 也和内置的 command id 'org.eclipse.ui.file.save' 和 'org.eclipse.ui.file.saveAll' 绑定在一起。这两个 command id 会在视图触发 p.firePropertyChange(ISaveablePart.PROP_DIRTY)) 事件时更新状态。
```java
public abstract class BaseEditorPart extends EditorPart {

  protected IDirtyable dirtyable = new Dirtyable(
      DirtyListener.createWeak(this, p -> p.firePropertyChange(ISaveablePart.PROP_DIRTY)));

  @Override
  public boolean isDirty() {
    return dirtyable.isDirty();
  }

  @Override
  public void doSave() {
  }

  @Override
  public void doSave() {
  }
}
```
当用户选中当前视图，且该视图实现了 ISaveablePart, such as EditorPart, eclipse 会根据该视图的 isDirty() 来判断是否应该 enable 'org.eclipse.ui.file.save', 如果用户点击了该 command, 那么就会调用对应的 doSave 方法。

当用户切换视图，eclipse 总会根据当前视图的 isDirty 来更新 command status. 当用户在当前视图 do some change, and want to trigger dirty, 仅仅让 isDirty 方法返回 true 是不够的，并不会理解刷新 command status， 需要执行 p.firePropertyChange(ISaveablePart.PROP_DIRTY) 来触发 command status update. 同样的，在 save 之后想让 dirty 消失，仅仅让 isDirty 返回 false 也是不够的，也需要执行 p.firePropertyChange(ISaveablePart.PROP_DIRTY) 来触发 command status update。

### Handler 扩展
每一个 command id 在被触发时，需要一个对应的 handler 来响应
```xml
   <extension
         point="org.eclipse.ui.handlers">
      <handler
            class="com.rcp.plugin.app.language.SwitchLanguageHandler"
            commandId="com.rcp.plugin.app.command.language">
      </handler>
   </extension>
```
每一个 handler 都需要实现 org.eclipse.core.commands.AbstractHandler, 当对应的 command id 被触发时，就会执行对应的 handler

### ViewPart 扩展
当想自定义一个视图时，需要扩展 org.eclipse.ui.views
```xml
   <extension
         point="org.eclipse.ui.views">
      <view
            allowMultiple="true"
            class="com.rcp.plugin.perspective.view.MasterViewPart"
            id="com.rcp.plugin.perspective.view.MasterViewPart"
            name="Master ViewPart"
            restorable="true">
         <description>
            This is the description for Master ViewPart
         </description>
      </view>
      <view
            allowMultiple="true"
            class="com.rcp.plugin.perspective.view.SlaveViewPart"
            id="com.rcp.plugin.perspective.view.SlaveViewPart"
            name="Slave ViewPart"
            restorable="true">
      </view>
   </extension>
```
每一个 ViewPart 需要继承 org.eclipse.ui.part.ViewPart, 然后在扩展的 class 属性里指定该实现了 ViewPart 的类，同时，也要给该 ViewPart 指定 unique id，该id 可以被用来直接打开该 ViewPart.

ViewPart 最重要的方法是 createPartControl, 用来创建该 part 的具体内容. 需要注意的是， ViewPart 如果有自己独有的 toolbar item 或者 menu item, 需要在该方法中添加。EditorPart 有自己专门的 EditorActionBarContribution 来添加。
```java
  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new GridLayout(1, false));
    // init the ui
    DemoComposite1 demoComposite1 = new DemoComposite1(parent, SWT.NONE, dirtyable);
    demoComposite1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
    FamilyTreeViewerComposite familyTreeViewerComposite = new FamilyTreeViewerComposite(parent, SWT.NONE);
    familyTreeViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // ViewPart does not support action bar contributor of extension point, need to custom define action bar through code
    // init menu
    IMenuManager mm = getViewSite().getActionBars().getMenuManager();
    WarningAction wa = new WarningAction();
    wa.setText("Warning");
    mm.add(wa);
    mm.update(true);

    // init toolbar
    IToolBarManager tm = getViewSite().getActionBars().getToolBarManager();
    tm.add(wa);
    tm.update(true);
  }
```
### EditorPart 扩展
EditorPart 和 ViewPart 扩展点是不一样的，它有一些自己独有的配置。
```xml
   <extension
         point="org.eclipse.ui.editors">
      <editor
            class="com.rcp.plugin.perspective.editor.FamilyInfoEditorPart"
            contributorClass="com.rcp.plugin.perspective.editor.contribution.FamilyEditPartActionBarContributor"
            default="false"
            id="com.rcp.plugin.perspective.editor.FamilyInfoEditorPart"
            name="Family Information Editor Part">
      </editor>
      <editor
            class="com.rcp.plugin.perspective.editor.PeopleInfoEditorPart"
            default="false"
            id="com.rcp.plugin.perspective.editor.PeopleInfoEditorPart"
            name="People Information Editor Part">
      </editor>
   </extension>
```
同样的，EditorPart 需要指定自己的实现类和 unique id 以及自己的 contributionClass. 这个 contributionClass 需要继承 EditorActionBarContributor, 在这个类里，实现对顶级菜单的更新。
```java
public class FamilyEditPartActionBarContributor extends EditorActionBarContributor {

  @Override
  public void contributeToMenu(IMenuManager menuManager) {
    // will add to main workbench menu
    super.contributeToMenu(menuManager);
    MenuManager mm = new MenuManager("Family Editor");
    menuManager.add(mm);

    WarningAction action = new WarningAction();
    action.setText("Family menu warning");
    mm.add(action);
  }

  @Override
  public void contributeToToolBar(IToolBarManager toolBarManager) {
    // will add to main toolbar, and only be visible when select family editor part
    super.contributeToToolBar(toolBarManager);
    WarningAction action = new WarningAction();
    action.setText("Family coolbar warning");
    toolBarManager.add(action);
  }

}
```
当打开该编辑器时，对应的菜单就会出现，只有当关掉所有该类型的编辑器，对应的菜单才会隐藏。

EditorPart 有两个重要的方法，init 和 createPartControl. init 在 createPartControl 之前执行
```java
public class FamilyInfoEditorPart extends BaseEditorPart {

  public static final String ELEMENT_EDITOR_PART_ID = "com.asml.rcp.plugin.perspective.editor.FamilyInfoEditorPart";

  private FamilyEditorInput familyEditorInput;

  private FamilyInfoEditComposite familyEditComposite;

  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    setSite(site);
    setInput(input);
    this.familyEditorInput = (FamilyEditorInput) input;
  }
  
  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new FillLayout());
    familyEditComposite = new FamilyInfoEditComposite(parent, SWT.NONE);
    familyEditComposite.setInput(familyEditorInput.getFamilyInformation());

    // have to call this in editor to apply the update
    setPartName(familyEditorInput.getName());
    setTitleImage(familyEditorInput.getImageDescriptor().createImage());

    // for EditorPart, user can define action bar through EditorActionBarContributor of extension point
    // still could custom define action bar through code
    // getEditorSite().getActionBars().getToolBarManager().add(new WarningAction());

  }

}
```
对于每一个 EditorPart, 它总是和一个文件或者一个对象绑定在一起。该对象需要实现 org.eclipse.ui.IEditorInput 接口，当尝试打开同一种类型的多个对象时，EditorPart 会通过 equals 方法判断两个input 是否为同一个，如果是，则不会用两个 EditorPart 打开相同的 input 对象。

打开 EditorPart 的方法，
```java
  public static void openEditor(IEditorInput input, String editorId) {
    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId);
    } catch (PartInitException exception) {
      exception.printStackTrace();
    }
  }
```

### Perspective 扩展
透视图是一组视图和编辑器的 layout 控制。同样的，每一个透视图需要指定自己的实现类和 unique id.
```xml
   <extension
         point="org.eclipse.ui.perspectives">
      <perspective
            class="com.asml.rcp.plugin.perspective.DemoPerspective"
            fixed="false"
            id="com.asml.rcp.plugin.perspective.DemoPerspective"
            name="Demo Perspective">
      </perspective>
   </extension>
```
其中， DemoPerspective 需要实现 org.eclipse.ui.IPerspectiveFactory, 一般来讲，都是通过编辑区的相对位置来放置其它视图。
```java
public class DemoPerspective implements IPerspectiveFactory {
  
  @Override
  public void createInitialLayout(IPageLayout layout) {
    layout.addView(MasterViewPart.MASTER_VIEW_ID, IPageLayout.LEFT, 0.46f, IPageLayout.ID_EDITOR_AREA);
    layout.addView(SlaveViewPart.SLAVE_VIEW_ID, IPageLayout.BOTTOM, 0.5f, IPageLayout.ID_EDITOR_AREA);
    layout.addView(MasterViewPart.MASTER_VIEW_ID + ":1", IPageLayout.RIGHT, 0.2f, IPageLayout.ID_EDITOR_AREA);
  }
}
```
需要注意的是，如果某一种视图在相同或者不同的透视图中被引用，并且想让该视图是各自独立的，那么需要在 view part id 后面加上 second id, such as MasterViewPart.MASTER_VIEW_ID:1, 否则具有相同 id 的视图会是共享的。

打开 Perspective 的方法，
```java
  public static void openPerspective(String perspectiveId) {
    try {
      PlatformUI.getWorkbench().showPerspective(perspectiveId, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    } catch (WorkbenchException exception) {
      exception.printStackTrace();
    }
  }
```
在打开应用程序的时候，一般需要指定一个默认的透视图，这是在 ApplicationWorkbenchAdvisor 里完成的
```java
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }
    
    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_INTRO, true);
        PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);
    }

    public String getInitialWindowPerspectiveId() {
	return "com.rcp.plugin.perspective.DemoPerspective";
    }
}
```
### Bindings 扩展
很多时候，我们需要为常用的 command 添加快捷键，比如 open, save 之类的，这时就需要 bindings 扩展。
```xml
   <extension
         point="org.eclipse.ui.bindings">
      <key
            commandId="org.eclipse.ui.help.helpContents"
            contextId="org.eclipse.ui.contexts.window"
            schemeId="org.eclipse.ui.defaultAcceleratorConfiguration"
            sequence="CTRL+H">
      </key>
   </extension>
```
这里，为eclipse 内置的 org.eclipse.ui.help.helpContents 添加快捷键 'CTRL + H', contextId 表示能够激活当前快捷键操作的使用范围， schemeId 表示使用哪种键盘绑定方式，这里都使用默认配置。

## RCP Extension Points
我们不仅可以使用其它插件提供的扩展点，还可以创建自己的扩展点供其它插件使用。


