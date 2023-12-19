# browser
JS 在浏览器环境里运行时  
![](asserts/browser.png)  

window 是一个根对象，也是 JS 代码的全局对象。  
* DOM => Document Object Model, 用于处理，显示页面内容
* BOM => Browser Object Model, 浏览器提供的用于处理 DOM 之外所有内容的对象

BOM 是浏览器提供的用于处理所有 非 DOM 的事务
* navigator 提供了浏览器和操作系统等信息
* location 允许我们读取当前 URL, 并且可以重定向到其它 URL
* 还有类似于 setTimeout, alert, 等一系列浏览器方法