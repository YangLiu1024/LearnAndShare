简单创建一个 project 后，在 html 里面 import index.js, everything is simple
```html
<html lang="en">
<head>
    <title>Bundle</title>
</head>
<body>
    <script src="../src/index.js"></script>
</body>
</html>
```
```js
// index.js
console.log('hello world')
```
after open the page in browser, it will log 'hello world' in console.  
然后尝试使用 esmodule 语法从 lodash module 里 import camelCase 方法，然后使用它
```js
import {camelCase} from 'lodash'
console.log(camelCase('hello world'))
```
然后我们就会看到一个错误
```bash
Uncaught SyntaxError: Cannot use import statement outside a module (at index.js:1:1)
```
这是因为 浏览器不知道从哪里加载 *lodash*, 为了解决这个问题，需要引入 webpack
```bash
npm i --save-dev webpack webpack-cli
```
然后在 package.json 里添加 build 命令
```json
{
  "name": "bundle",
  "main": "index.js",
  "scripts": {
    "build": "webpack"
  },
  "dependencies": {
    "lodash": "^4.17.21"
  },
  "devDependencies": {
    "webpack": "^5.83.1",
    "webpack-cli": "^5.1.1"
  }
}
```
在命令行执行 *npm run build* 后，生产环境下能使用的 bundle 文件就产生了。将 html 文件里面引用的 index.js 替换成生成的 bundle 文件
```html
<html lang="en">
<body>
    <script src="../dist/main.js"></script>
</body>
</html>
```
然后就能看到网页 console 打印了 camleCase 的 helloWorld.  
到现在为止，我们都只是使用 webpack 的默认配置，为了能够自定义配置，需要创建 *webpack.config.js* 文件
```js
//webpack.config.js, 使用 commonjs 语法
const path = require('path')
module.exports = {
    entry: './src/index.js',
    output: {
        filename: 'index.js',
        path: path.resolve(__dirname, 'dist')
    }
}
```
当我们想使用一些 css 样式时，我们可以创建一个新的 *scss* 文件
```scss
// style.scss
$text: orange;
$bg: black;

body {
    color: $color;
    background: $bg;
}

```
然后在代码里 导入
```js
import './style.scss'
import {camelCase} from 'lodash'

console.log(camelCase('hello world'))
```
然后再次执行 *npm run build*, 就会出现一个新的错误
```bash
ERROR in ./src/style.scss 4:5
Module parse failed: Unexpected token (4:5)
You may need an appropriate loader to handle this file type, currently no loaders are configured to process this file.
```
webpack 不知道该怎么解析 scss 文件，所以我们需要配置以下 scss 文件对应的 loader.  
首先需要安装所需的 loader
```bash
npm i --save-dev css-loader style-loader sass-loader
```
然后在 *webpack.config.js* 里配置对应的 loader, 这样就可以加载 scss 文件了
```js
module.exports = {
    module: {
        rules: [
            {
                test: /\.s[ac]ss$/i,
                use: [
                    // Creates `style` nodes from JS strings
                    "style-loader",
                    // Translates CSS into CommonJS
                    "css-loader",
                    // Compiles Sass to CSS
                    "sass-loader",
                ]
            },
        ]
    }
}
```
除了设置一些 loader 之外，还可以使用一些 plugin
```bash
npm install --save-dev webpack-bundle-analyzer
```
安装了 plugin 之后，就可以使用了
```js
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin
module.exports = {
    plugins: [
        new BundleAnalyzerPlugin()
    ]
}
```