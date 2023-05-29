//Declaration or statement expected. This '=' follows a block of statements, so if you intended to write a destructuring assignment, you might need to wrap the whole assignment in parentheses.
// 如果想使用 解构 的方式，在 commonjs 里，需要把整个表达式用 () 括起来
//({mod1Function, mod1Function2} = require('./mod1.js'))
//mod = require('./mod1.js')

// 还可以使用如下的解构语法
const {mod1Function, mod1Function2} = require('./mod1.js')
// can not import es module from commonjs module
// const {Hello} = require('./mod2.mjs')
const testFunction = () => {
    console.log('Im the main function')
    mod1Function()
    mod1Function2()
}

testFunction()