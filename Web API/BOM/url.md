# URL
URL 用来表示唯一资源，其格式为 ***protocol://username:password@hostname:port//pathname?searchParam#fragment***
URL 构造函数会处理 分隔符 ***/***
```js
// url 是绝对或者相对路径，若是相对路径，则需要指定 base
new URL(url)
new URL(url, base)
```

## 实例属性
* hash => URL 中 从'#' 开始到结尾的部分, fragment 部分并不会被编码
* host => hostname(:port), port 为可选
* hostname => 域名
* port => 端口
* href => 完整的 URL string
* origin => 返回 protocol://hostname:port 
* username => 用户名
* password => 密码
* protocol => 协议，http,https,file,data...,包含协议后的 ***：***
* pathname => 去掉 host, 初始为 '/' 和剩余路径的 字符串，若没有，则为空
* search => 从 '?' 开始到后续的参数
* searchParams => '?' 后续的参数
## 示例方法
* toString() => 返回 href
* toJSON() => 和 toString() 一样
## 静态方法
* canParse(url)
* canParse(url, base) => 快速检测输入是否能够正确构造 URL, 避免直接调用构造函数然后抛出异常
* createObjectURL(blob) => 创建一个 blob 对象对应的 blob 链接，只在当前 document 上下文里有效
* revokeObjectURL(url) => 释放一个已经在当前 document 上下文里注册过的 URL 对象，通过调用该方法，让浏览器知道不用在内存里继续保留对这个数据的引用了