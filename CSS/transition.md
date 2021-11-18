## CSS transform
CSS 支持以下 2D transformation methods:
* translate(x, y), such as translate(50px, 100px)
* rotate(deg), such as rotate(20deg), or rotate(-20deg)
* scale(xk, yk), such as scale(2, 2), 在 x, y 方向都放大两倍
* scaleX(xk)
* scaleY(tk)

## CSS transition
CSS 支持给指定元素赋予 transition 效果，形式为 <code>transition: property duration curve delay</code>,  
* *property* 为想要赋予过渡效果的 style 参数，可以是任意 style, 包括 *transform*, 也可以是 *all*, 表示任意 style
* *duration* 为过渡持续时间， 
* *curve* 为计算起始效果和目标效果之间的内插函数。  
* *delay* 是过渡的延迟时间
   
比如：下例会在 2s 内平滑的将 width 从 100px 增加到 300px, 如果不再 hover, 也会平滑的从300px 减小到 100px
```css
div {
  width: 100px;
  transition: width 2s
}
div:hover {
  width: 300px
}
```
curve 有以下选项：
* ease, 这个过渡有较慢的 start, 快速的中间过渡，较慢的结尾，这是 default option
* linear, 从开始到结束是匀速的
* ease-in, 有一个较慢的 start
* ease-out, 有一个较慢的 end
* ease-in-out, 有较慢的 start 和 end
* cubic-bezier(n,n,n,n), 通过 cubic-bezier function 自定义 value

除了通过 *transition*, 还可以拆开分别定义，比如 *transition-property*, *transition-duration*, *transition-timing-function*, *transition-delay*。 
## CSS Animation
CSS 支持不通过 JS 或者 Flash，直接为 HTML elements 赋予动画。  
CSS 动画就是让 HTML elements 从一个 style 逐渐变化到另一个 style, 它是通过 @keyframes 来完成的，@keyframes 里存储了在确定时间点需要展示的 styles. 不同时间点，就可以切换不同 styles.  
为了让动画起效，需要把 动画 bind 给 HTML element
```css
@keyframes example {
  from {background-color: red;}
  to {background-color：yellow;}
}

div {
  animation-name: example;
  animation-duration: 2s;
  background-color: red;
}
```
动画结束后，会回到元素的原始 style. 注意，必须指定 animation-duration, 否则动画不会起效，因为其默认值为 0.  
除了使用 start, to, 还可以使用更为精细的百分比
```css
@keyframes example {
  0%   {background-color: red;}
  25%  {background-color: yellow;}
  50%  {background-color: blue;}
  100% {background-color: green;}
}

div {
  background-color: red;
  animation-name: example;
  animation-duration: 4s;
}
```
同样的，CSS animation 也可以 delay, *animation-delay: 2s*, 这样，则会延迟 2s 开始动画。注意参数还可以是负数 -N，负数表示该动画 will start as if it has already been playing for N seconds. 比如动画本来是 4s, 延时 -2s, 那么动画就会看起来像是直接从第 2s 的状态开始的。  
有的动画可能需要 play 多次，则可以通过 *animation-iteration-count: 3* 来设置，这样动画就会 play 3 次， 当然也可以使用 *infinite* 来表示动画永不停止。  
有的时候，动画可以倒着播放，这就可以通过参数 *animation-direction* 来控制，值为*normal*, 则正常顺序播放，值为 *reverse*, 则倒叙播放。有的时候想先顺序，再倒叙，再顺序，依次循环，就可以使用 *alternate*, 当然，播放次数还是受 *animation-iteration-count* 限制的，想先倒序，再顺序，则是 *alternate-reverse*.  
同样的，动画也有内插函数，和 *transition-timing-function* 一样。 
动画也可以被暂停或者继续播放，或者重置，这就通过属性 *animation-play-state* 里完成，该属性有2个常用选项，*paused*, *running*.