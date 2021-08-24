D3 allows you to bind arbitrary data to a Document Object Model (DOM), and then apply data-driven transformations to the document.

# Selections
D3 employs a declarative approach, operating on arbitrary sets of nodes called selections
```js
d3.selectAll("p").style("color", "blue");
```
D3 provides numerous methods for mutating nodes: setting attributes or styles; registering event listeners; adding, removing or sorting nodes; and changing HTML or text content.

# Dynamic Properties
Yet styles, attributes, and other properties can be specified as functions of data in D3, not just simple constants.
```js
d3.selectAll("p").style("color", function() {
  return "hsl(" + Math.random() * 360 + ",100%,50%)";
});
```
To alternate shades of gray for even and odd nodes:
```js
d3.selectAll("p").style("color", function(d, i) {
  return i % 2 ? "#fff" : "#eee";
});
```
Computed properties often refer to bound data. Data is specified as an array of values, and each value is passed as the first argument (d) to selection functions. With the default join-by-index, the first element in the data array is passed to the first node in the selection, the second element to the second node, and so on. For example, if you bind an array of numbers to paragraph elements, you can use these numbers to compute dynamic font sizes
```js
d3.selectAll("p")
  .data([4, 8, 15, 16, 23, 42])
    //the first element in data array pass to first <p> element
    //如果data size > selected element size, 则多余的data 不会使用
    //如果 data size < selected element size, 则多余的element 不会apply 对应的 function
    .style("font-size", function(d) { return d + "px"; });
```
Once the data has been bound to the document, you can omit the data operator; D3 will retrieve the previously-bound data. This allows you to recompute properties without rebinding.
# Enter and Exit
Using D3’s enter and exit selections, you can create new nodes for incoming data and remove outgoing nodes that are no longer needed.

When *data* is bound to a selection, each element in the data array is paired with the corresponding node in the selection. 
```js
d3.select("body")
  .selectAll("p")//the body has only 4 paragrah actually
  .data([4, 8, 15, 16, 23, 42])
  .text(function(d) {return d;})//only the node has corresponding data element will be updated
```
If there are fewer nodes than data, the extra data elements form the *enter* selection, which you can instantiate by appending to the enter selection
```js
d3.select("body")
  .selectAll("p")//the body has only 4 paragrah actually
  .data([4, 8, 15, 16, 23, 42])//bind 6 data element to selection, in this case, data size > selected node size
  .enter()//the extra data elements form the enter selection
  .append("p")//append html element using extra data elements
  .text(function(d) { return "I’m number " + d + "!"; });//the d here could be 23 and 42, the extra data elements
```
if there are fewer data elements that nodes, the extra nodes form the *exit* selection
```js
d3.select("body")
  .selectAll("p")
  .data([])
  .exit()
  .remove()//all p node will be removed
```
A common pattern is to break the initial selection into three parts: updating, entering, exiting
```js
var p = d3.select("body")
  .selectAll("p")
  .data([1, 2, 3, 4, 5])
//updating
p.text(function(d) {return d})
//entering
p.enter().append('p').text(function(d) {return d})
//exiting
p.exit().remove()
```
# Transitions
D3’s focus on transformation extends naturally to animated transitions. Transitions gradually interpolate styles and attributes over time.

```js
d3.select("body").transition()
    .style("background-color", "black");

d3.selectAll("circle").transition()
    .duration(750)
    .delay(function(d, i) { return i * 10; })
    .attr("r", function(d) { return Math.sqrt(d * scale); });
```