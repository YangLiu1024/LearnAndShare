JS 在设计之初，只是为了作为网页里内嵌的小段代码使用，当后面发展起来后，大家意识到 module 的必要性。  
module 的作用在于，
1. 把代码进行分离，方便组织代码
2. 代码易于维护
3. 代码重用
4. 代码可见性控制

module 的实现方式有很多种，广泛使用的有两种
* commonjs, 开始于 2009, 使用于 server-side js app with node, browser 端不支持 common js。Nodejs 最开始只支持 commonjs, 现在也支持 es module 了。
* es module, 开始于 2015， 被 browser 和 nodejs 支持，用法更加 modern

一般来说，如果文件以 *.cjs* 结尾，表示是使用 *commonjs*，如果以 *.mjs* 结尾，表明是使用 *esmodule*.  
在 package.json 里面，还有一个属性 **type** 来说明到底使用什么类型，默认值是 *commonjs*, 当想要使用 esmodule 时，需要改为 *module*  
```node
// commonjs 使用 module.exports 来 export
module.exports = {***}
// 也可以使用缩写 exports
exports.field = value
// 使用 require 来 import
o = require(***) // o 是该module 导出的所有东西
（{field1, field2} = require(***)）// 如果使用解构，需要使用 () 把整个表达式括起来, 或者
const {field1, field2} = require(***)
```
这些文件里的 require, module, exports 是全局的吗？其实并不是，Commonjs 会把我们的 code wrap 到一个函数里
```js
(function(exports, require, module, __filename, __dirname) {
    // your code lives here
    // module 和 exports 有什么区别呢？
    // module 就是一个 plain js object, 它有一个名为 exports 的属性
    // exports 是一个变量，初始，它被赋值为 module.exports
    // 所以如果不改变 exports 的引用，那么它就一直指向 module.exports, 如果使用 exports = *** 的形式，exports 就不再指向 module.exports
    // nodejs 在每一个 module 文件末尾隐式 return module.exports to require function
    // 所以建议只使用 module.exports, 不使用 exports 
});
```
所以这些 keywords 总是 module specific.  
## Why Es module
Commonjs 看起来能够工作的很好，为什么还需要创造 esmodule 呢？  
commonjs 最开始是 nodejs 自带的 module system, Javascript 本身并没有一个 module system. ES2015 标准则定义了自己的 module system, 即 ES module. 之后，各大浏览器 vendor 和 nodejs 才开始实现该标准，支持 ES module.

```node
// package.jason
{
  "name": "esmodule",
  "license": "ISC",
  "type": "module" // 需要指定 type 为 module, 才能使用 esmodule 语法
}

// mod1.js
// 可以直接 export 变量定义
export const mod1Function = () => console.log('Mod1 is alive!')
export const mod1Function2 = () => console.log('Mod1 is rolling, baby!')

function sum(x, y) {
    return x + y;
}
// 还可以有 默认导出，默认导出的域无需解构，就能导入
export default sum;

const multi = (x, y) => x * y
// 可以使用对象来 export
export {
    multi,
};

// index.js
// 导入 default 导出，命名导出，以及在导入的时候重命名
// 这里的 import sum 其实也是 简写，它也是一个重命名导出 -> import {default as sum}
import sum, {mod1Function as func1, mod1Function2 as func2, multi} from './mod1.js';

// import all exports
import * as mod from './mod1.js';


const testFunction = () => {
    console.log('Im the main function')
    func1()
    func2()
    console.log(sum(1, 2), multi(2, 3))
}

testFunction()
```
需要注意的是，很多 npm package 并不是 es module, 而是 commonjs. 那么我们该怎么在 es module 里面导入这些 package 呢？Nodejs 帮我们解决了这个问题，它允许我们在 es module 里面导入 commonjs module, commonjs 的 *module.exports* 简单的作为 default 导出
## Difference between commonjs and es module
### file extension
当文件以 .js 结尾，如果 package.json 里面没有指定 type, 或者指定为 commonjs, 那么处理为 commonjs 类型. 如果指定了 *type: module*, 则会处理为 es module 类型。  
当文件以 .cjs 结尾，则一直以 commonjs 类型处理。  
当文件以 .mjs 结尾，则一直以 es module 类型处理。
### Dynamic vs. Static
除了导入导出的语法不同，两种模式导入导出的方式也有不同。  
commonjs 里的 导入总是在 runtime 被处理，即总是在运行 code 的时候，来处理 require.  
es module 里的 导入都是在 编译期处理的。如果必须使用动态加载，那么可以使用 *import()* function
# using module

# buildling modules
把代码分为多个 module 会带来一些好处，但是这些好处都只是针对开发阶段而言的。但在生产模式里，如果仍然分为多个 module, 那么浏览器在加载页面时，必然要去加载每一个文件，这必然会带来巨大的性能消耗。  
这个问题可以很好的被 *module bundler* 解决，它以多个 modules 为输入，输出是一个 single bundle file. 这样在生产环境下，浏览器只需要加载一个文件就行。  
那么有什么工具可以完成这一步骤呢？现在流行的有 Browserify, webpack 等， 以 webpack 举例
```npm
// 安装 webpack 依赖项
npm -i --save-dev webpack webpack-cli
```
```js
//webpack.config.js
const path = require('path');

module.exports = {
  entry: './main.js',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js',
  },
};
```
```json
{
  "name": "testappv2",
  "main": "main.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build": "webpack"
  },
  "devDependencies": {
    "webpack": "^5.72.0",
    "webpack-cli": "^4.9.2"
  }
}
```
```npm
npm run build
```
在 *npm run build* 之后，dist folder 下面就会生成我们想要的 bundle file 'bundle.js', 浏览器在加载页面之时，也就只会加载该文件。



