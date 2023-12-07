HTML 里元素的 *position* 有多种方式
* static, 默认方式，正常文档流摆放, 从左到右，从上到下，不会出现元素上下重叠的情况
* relative, 不管父元素是什么 position 方式，总是相对于 static 位置作出偏移，支持 top/bottom/left/right, 但元素并没有脱离文档流，它仍然会占据它原本该在的位置，之后的元素的摆放也会考虑到该元素的正常摆放位置。
* absolute, 相对于上一个 non-static 父元素进行摆放，如果找不到，则是 body 元素， 支持 top/bottom/left/right。元素会脱离文档流，不占位，就像它不存在一样，其它元素的摆放并不会考虑该元素。
* fixed, 相对于浏览器窗口进行定位，支持 top/bottom/left/right
* sticky, 在视窗范围内时，正常 static 摆放，当要滑出浏览器视窗时，固定在视窗边缘

可以看出，除了 static 方式，经过其它方式摆放的元素都可能会和其它元素存在重叠，那么在发生重叠的时候，到底哪一个元素摆放在上面，这个就需要使用 z-index 来决定。  
1. 首先，对于 static 和 non-staic 元素，static 元素相当于 z-index 为 0, 而 non-static 元素如果不指定 z-index, 其 z-index 值为 auto, z-index 为 auto 的元素会在 static 元素之上，且遵循后来居上的原则。    
2. 如果 non-static 元素设置 z-index 为负数，则会被 static 元素覆盖。如果指定了 z-index 为正数，则其会在 z-index 为auto 的元素之上。且越大的 z-index 在越上面。  

在上述规则里，有一个很重要的约束就是，不同元素 z-index 的比较，需要在相同 *层叠上下文* 里。什么是层叠上下文呢？ 相当于一个基准平台，在它里面的下一级 non-static 元素通过比较 z-index 来确定谁高谁低。  

那什么元素可以产生 层叠上下文 呢？  
1. body 元素
2. relative/absolute 定位的元素，且其 z-index 不为 auto
3. fixed/sticky 定位元素
4. flex/grid 容器的子元素，且 z-index 不为 auto
5. 元素有 opacity 且值小于 1
6. 有 transform
7. other...
一个元素所属的层叠上下文是从它出发，向根节点出发找到的第一个能产生 层叠上下文 的元素。  z-index 的比较，总是在拥有相同层叠上下文的元素之间进行的。  

产生了 层叠上下文 的元素就可以作为基准，来摆放它的下一级 non-static 元素的高低。需要注意的是，层叠上下文是可以嵌套的，即下一级的 non-static 元素仍然可以开启自己的 层叠上下文，它自己的下一级 non-static 元素又在它的基准上进行摆放。但子层叠上下文是被限制在了父层叠上下文里。  
举例来说，有一个 relative div 元素，开启了 层叠上下文，它有两个 non-static 子元素，一个 z-index 是 2， 一个是 3. 显然，3 会在 2 之上。 2 也有一个 non-static 子元素，它的 z-index 为 5，虽然 5 看起来最大，但是它的层叠上下文是 2 这个元素，那么 5 作为 2 的子元素，会在 2 之上，但也会在 3 之下。这里的 5 只会跟拥有和它相同层叠上下文的元素比较。  

总的来说，z-index 只对 non-static 元素起作用。当所有元素都是 static 元素时，就是正常的文档流。  
当元素设置了 non-static position, 但是不设置 z-index 时，其 z-index 为默认值 auto. z-index 为 auto 的元素会在 static 元素之上，且遵循后来居上的原则。  
当 non-static 元素设置了 z-index 时，首先，该元素会创建自己的 层叠上下文，其次，它会和其它和它拥有相同层叠上下文的 non-static 元素进行 z-index 比较。  

***换言之，两个元素进行高度比较时，如果两个元素处于相同层叠上下文，则根据 z-index 来比较。即使两个元素是父子或者祖孙关系，仍然可以处于相同的层叠上下文里。如果两个元素处于不同的层叠上下文，则先找到共同的祖先层叠上下文，然后比较共同层叠上下文下这两个元素所在的局部层叠上下文的层叠水平***
