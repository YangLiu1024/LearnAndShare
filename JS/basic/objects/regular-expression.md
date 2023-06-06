JS 里， 正则表达式也是对象， 对象类型是 RegExp
```js
let re = /ab+c/
console.log(re.__proto__ === RegExp.prototype)//true
//等价于通过构造器构造
let re = new RegExp('ab+c')
```
methods
* exec()
* test()

在字符串里，也有方法可以直接使用正则表达式
* match()
* matchAll()
* replace()
* replaceAll()
* search()
* split()