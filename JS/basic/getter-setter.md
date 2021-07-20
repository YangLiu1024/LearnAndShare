the `get` syntax 是给对象的 property 绑定一个 function, 这个 function 会在查找该属性时调用
the `set` syntax 是给对象属性绑定一个函数，在尝试给该属性赋值时调用
当调用 getter/setter 时，对应函数体内的 this 会绑定到调用 getter/setter 的对象上
```js
{
    //get 函数不能有参数
    get propname() {...},//可以直接指定属性名字
    get [expression]() {...}//也可以通过表达式来计算出属性名字
    //并且这个名字不能和其它属性名相同

    //set 函数只能有一个参数， 并且它的名字不能和其它属性名相同
    set propname(val) {...}
    set [expression](val) {...}
}
```

Example
```js
const obj = {
  log: ['example','test'],
  get latest() {
    if (this.log.length === 0) return undefined;
    return this.log[this.log.length - 1];
  }
}
console.log(obj.latest);//test
obj.latest = 'other'//并不会起效, 但是也可以再给 latest 设置 setter

const obj = {
  log: ['example','test'],
  get latest() {
    if (this.log.length === 0) return undefined;
    return this.log[this.log.length - 1];
  },
  set latest(value) {
      if (this.log.length === 0) {
          return
      } else {
          this.log[this.log.length - 1] = value
      }
  }
}

//同样的，也可也通过 Object.defineProperty 来定义 getter/setter
// defineProperty 不能同时给属性设置 writable/value 和 getter/setter, 也就是说一个对象属性的 descriptor 要么设置 getter/setter, 要么设置 writable/value
let o = {}
Object.defineProperty(o, 'a', {
    writable: false,
    value: 1,
    enumerable: true,
    configurable: false
})
o.a //1
o.a = 2//不起效

Object.defineProperty(o, 'b', {
    enumerable: true,
    configurable: false
    get() {
        return 2
    }
})
```
一般来说， getter/setter 适合作为计算属性，即依赖于其它属性的值，需要通过一定的计算推导出来。getter 的形式也可也保证这个额外的计算是 lazy的，只会在真正被调用时，才会计算