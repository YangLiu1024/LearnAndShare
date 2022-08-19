# Key & Ref
React 有一些特殊的关键字，比如 key, ref
## Key
在使用 组件的 list 时，react 要求为每个组件指定一个 unique key
```js
function List({items}) {
    return (
        <ul>
            {
                items.map(i => <li key={i.id}>{i}</li>)//必须为数组内的每一个元素指定 key
            }
        </ul>
    )
}
```
需要注意的是，key 并不是 props, 并不会从父组件传递到子组件。
```js
function ListItem({item}) {
    return (
        <li key={item.id}>{item}</li>
    )
}

function List({items}) {
    return (
        <ul>
            {
                items.map(i => <ListItem item={i}/>)
            }
        </ul>
    )
}
```
上述写法仍然是错误的，React 仍然会报warning, 因为 React 是要求数组里的每个元素都有 unique key, 显然，组件 ListItem 并没有 指定 unique key, 即使 render　内容指定了 key.  
所以正确做法，是给 *ListItem* 而不是 *ListItem* 的根元素指定 key
```js
function ListItem({item}) {
    return (
        <li>{item}</li>
    )
}

function List({items}) {
    return (
        <ul>
            {
                items.map(i => <ListItem key={i.id} item={i}/>)
            }
        </ul>
    )
}
```

## ref
与 key 类似，ref 也不是 prop, 并不会从父组件传递给子组件。  
***React 不支持将 ref 赋给函数组件，因为函数组件并没有实例，只能将 ref 绑定在 DOM 元素或者 Class 组件上***
```js
// FancyButton 包装了一个 button element
class FancyButton extends React.Component {
    render() {
        return (
            <button className="FancyButton">
                {this.props.name}
            </button>
         );
    }
}

function App() {
    const ref = useRef();
    // 这里的 ref 指向 FancyButton 这个组件, 有的时候，想让赋给组件的ref 重新赋给组件内其它元素
    return (
        <FancyButton ref={ref}/>
    )
}
```
为了提供这种可能性，React 提供了 forwardRef 方法，将传递给组件的 ref 获取，然后按照自己的需求操作，比如将 ref 绑定在组件内的任意元素上
```js
// 对于 class component, forwardRef 的使用稍微复杂一些
class FancyButton extends React.Component {
    render() {
        return (
            <button ref={this.props.innerRef} className="FancyButton">
                {this.props.name}
            </button>
         );
    }
}
// 为了在使用组件的时候，仍然可以使用 ref, 我们需要在 class component export 的时候，调用 forwardRef, 通过使用 innerRef(当然也可以是其它任意名字，不是 ref 就行) 的方式来
export default React.forwardRef((props, ref) => (
    <FancyButton innerRef={ref} {...props}>
))
```
对于 函数组件，使用起来就简单一些
```js
// 现在赋给组件 FancyButton 的 ref 就绑定在了组件内的 button 元素上
const FancyButton = React.forwardRef((props, ref) => (
    <button ref={ref} className="FancyButton">
      {props.children}
    </button>
))
```
ref 也接受 callback function, 当组件 mount 时，参数是组件实例或者 DOM 元素，当组件 unmount 时，参数是 null