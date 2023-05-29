// esmodule 使用 import ... from ... 的格式
// 在 node 环境，如果想使用 esmodule 特性，需要在 package.json 里面，注明 type: 'module'
import sum, {mod1Function as func1, mod1Function2 as func2, multi} from './mod1.js';

// import all exports
//import * as mod from './mod1.js';

// import cjs module in es module file
import * as Hello from './mod2.cjs'

const testFunction = () => {
    console.log('Im the main function', Hello.Hello)
    func1()
    func2()
    console.log(sum(1, 2), multi(2, 3))
}

testFunction()