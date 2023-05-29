// esmodule 默认没有提供 __dirname 和 __filename 的功能
// 为了在 esmodule 里有类似功能，可以通过以下方法实现
// import { fileURLToPath } from 'url'
// import { dirname, resolve } from 'path'
// const __filename = fileURLToPath(import.meta.url)// 当前文件的绝对路径
// const __dirname = dirname(__filename)// 当前文件的 parent folder 的绝对路径

// export default {
//     entry: './src/index.js',
//     output: {
//         filename: 'index.js',
//         path: resolve(__dirname, 'dist')
//     },
//     module: {
//         rules: [
//             {
//                 test: /\.scss$/,
//                 use: [
//                     'style-loader',
//                     'css-loader',
//                     'sass-loader'
//                 ]
//             },
//         ]
//     }
// }

// 或者不要在 package.json 里面指明使用 esmodule, 直接使用 commonjs
const path = require('path')
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin

module.exports = {
    entry: './src/index.js',
    output: {
        filename: 'index.js',
        path: path.resolve(__dirname, 'dist')
    },
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
    },
    plugins: [
       // new BundleAnalyzerPlugin()
    ],
}
