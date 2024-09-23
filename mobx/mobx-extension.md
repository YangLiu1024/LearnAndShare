# mobx 中的继承
mobx 对子类的支持是有限制的，子类只能重新定义原型链上的 action/computed/flow, 而且不能重新定义字段声明, 也不支持私有域 #field。
```js
class Parent {
    // 被注释的实例字段不可被重新定义
    observable = 0
    arrowAction = () => {} // 箭头函数属于实例，不属于原型链，

    // 未被注释的实例字段可以被重新定义
    overridableArrowAction = action(() => {})

    // 被注释的原型methods/getters可以被重新定义
    action() {}
    actionBound() {}
    get computed() {}

    constructor(value) {
        makeObservable(this, {
            observable: observable,
            arrowAction: action
            action: action,
            actionBound: action.bound,
            computed: computed,
        })
    }
}

class Child extends Parent {
    /* --- 继承来的定义 --- */
    // 抛出 - TypeError: Cannot redefine property
    // observable = 5
    // arrowAction = () = {}

    // OK - 未被注释的
    overridableArrowAction = action(() => {})

    // OK - 原型
    action() {}
    actionBound() {}
    get computed() {}

    /* --- 新的定义 --- */
    childObservable = 0;
    childArrowAction = () => {}
    childAction() {}
    childActionBound() {}
    get childComputed() {}

    constructor(value) {
        super()
        makeObservable(this, {
            // 继承来的
            action: override,
            actionBound: override,
            computed: override,
            // 新的
            childObservable: observable,
            childArrowAction: action
            childAction: action,
            childActionBound: action.bound,
            childComputed: computed,
        })
    }
}
```