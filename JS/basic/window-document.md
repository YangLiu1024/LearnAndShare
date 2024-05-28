window 是浏览器在加载页面时，第一个加载的全局对象。可以理解为一个 tab 页面，对应着一个 window 对象。  
window 对象里有很多属性和方法，比如 document 以及 setTimeout 等。  
document 就是 window.document, 是同一个对象，document 表示的是当前页面所对应的 DOM js 对象。

# 弹窗
每一个 tab 其实都是一个 window, window.open(url, name, options) 会打开一个新的 window. 弹窗是一个独立的窗口，具有自己独立的 JS 环境。  
options 用来配置窗口，比如位置，大小，是否显示浏览器菜单等