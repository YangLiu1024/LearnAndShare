d3 can select html element by class, id, tag name

# Selections
* d3.select -> select first matched element
* d3.selectAll -> select all matched elements
* selection.select() -> select first matched descendant element for each node in selection. If no element matches the specified selector for the current element, the element at the current index will be null in the returned selection. if current element has associated data, this is data is propagated to the corresponding selected element. If the selector is a function, it is evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element (nodes[i]). It must return an element, or null if there is no matching element. 
* selection.selectAll() -> select all matched descendant element for each node in selection, and does not propagated data to selected children element
```xml
<div>
<p>1 paragraph<b>B1</b></p>
<p>2 paragraph</p>
<p>3 paragraph<b>B2</b>asdjkl <b>B3</b></p>
<p>4 paragraph</p>
</div>
```
```js
d3.selectAll('p').data([1, 2, 3, 4]).select('b').text((d, i) => {
console.log(d, i)
return i
})
//since only first and third 'p' has 'b', so the data of 'p' passed to its selected 'b'
//for second 'p' and fourth 'p', the selection returned is null
//so the selection is something like ['b>B1</b>', null, '<b>B2</b>', null], this is why the output index is 0 and 2
//output
//1, 0
//3, 2

d3.selectAll('p').data([1, 2, 3, 4]).selectAll('b').text((d, i) => {
console.log(d, i)
return i
})
//for selection.selectAll, the data is not passed to selected children by default
//and the selection for children will be grouped,
//the selection is something like [['<b>B1</b>'], ['<b>B2</b>', '<b>B3</b>']]
//output
//undefined, 0
//undefined, 0
//undefined, 1

d3.selectAll('p').data([1, 2, 3, 4]).selectAll('b').data([1,2,3,4]).text((d, i) => {
console.log(d, i)
return i
})
//the data is assigned to each group
//output
//1, 0
//1, 0
//2, 1

const matrix = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9]
]

d3.selectAll('p')
.data(matrix)
.selectAll('b')
//the d is the group's parent selection's datum
.data((d, i) => d)
.text((d,i) => d)

//if the datum has been assigned to elements, its stored in the __data__ field
//so the datum is sticky to element to be easy to used in re-selection
d3.selectAll('p')
.selectAll('b')
.text((d,i) => d + d)//
```

* selection.filter(filter) -> the filter could be a selector string or function, such as d3.selectAll('tr').filter(':nth-child(even)') or d3.selectAll('tr').filter((d, i) => i & 1)
* selection.selectChild([selector]) -> Returns a new selection with the (first) child of each element of the current selection matching the selector. If no selector is specified, selects the first child (if any). If the selector is specified as a string, selects the first child that matches (if any). If the selector is a function, it is evaluated for each of the children nodes, in order, being passed the child (child), the child’s index (i), and the list of children (children); the method selects the first child for which the selector return truthy, if any.
* selection.selectChildren([selector]) -> Returns a new selection with the children of each element of the current selection matching the selector. If no selector is specified, selects all the children. If the selector is specified as a string, selects the children that match (if any). If the selector is a function, it is evaluated for each of the children nodes, in order, being passed the child (child), the child’s index (i), and the list of children (children); the method selects all children for which the selector return truthy

# Modify Elements
After selecting elements, use the selection’s transformation methods to affect document content.
* text('hello world') -> change the text of selected nodes
* html('<p></p>') -> change the inner html of selected nodes
* attr('attribute name', 'value') -> update the value of specifed attribute
* style('style name', 'value') -> update the value of specified style
* classed('classs name', flag) -> if flag is true, add the class to selected node, if not, remove the class from selected nodes
* property(name[, value]) -> some HTML elements have special properties that are not addressable using attributes or styles, such as *value* for form text input, *checked* for checkbox
* append(type) -> appends a new element of this type (tag name) as the last child of each selected element, if selected element has data, will pass to new created element. if the selection is *enter* selection, append the element at the end of the parent selection one by one
* insert(type[, before]) -> insert elements before the specfied selector *before*, such as d3.selectAll('div').insert('p', ':first-child')
* remove() -> remove the selected elements
* clone([deep]) -> Inserts clones of the selected elements immediately following the selected elements and returns a selection of the newly added clones. If deep is truthy, the descendant nodes of the selected elements will be cloned as well. Otherwise, only the elements themselves will be cloned, equals to create a empty element and insert following the selected elements
* sort(compare) -> Returns a new selection that contains a copy of each group in this selection sorted according to the compare function. After sorting, re-inserts elements to match the resulting order (per selection.order). The compare function, which defaults to ascending, is passed two elements’ data a and b to compare. It should return either a negative, positive, or zero value. If negative, then a should be before b; if positive, then a should be after b; otherwise, a and b are considered equal and the order is arbitrary.
* create(name) -> Given the specified element name, returns a single-element selection containing a detached element of the given name in the current document. This method assumes the HTML namespace, so you must specify a namespace explicitly when creating SVG or other non-HTML elements


# Join Data
* data([data[,key]]) -> bind the data element to current selection, return a new selection that represent the *update* selection(the elements successfully bound to data). if a data has been assigned to an element, it is stored in the property *__datat__*, thus making the data 'sticky' and available on re-selection
* enter() -> when data elements size > selected html elements size, the extra data elements form the enter() selection. Conceptually, the enter selection’s placeholders are pointers to the parent element 
* exit() -> when data elements size < selected html elements size, the extra html elements form the exit() selection
* join(enter[,update][,exit]) -> Appends, removes and reorders elements as necessary to match the data that was previously bound by selection.data, returning the merged enter and update selection
* datum([value]) -> Gets or sets the bound data for each selected element. Unlike selection.data, this method does not compute a join and does not affect indexes or the enter and exit selections. If a value is specified, sets the element’s bound data to the specified value on all selected elements. If the value is a constant, all elements are given the same datum; otherwise, if the value is a function, it is evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element (nodes[i]). The function is then used to set each element’s new data. A null value will delete the bound data. If a value is not specified, returns the bound datum for the first (non-null) element in the selection. This is generally useful only if you know the selection contains exactly one element.
* merge(other) -> Returns a new selection merging this selection with the specified other selection or transition. The returned selection has the same number of groups and the same parents as this selection. Any missing (null) elements in this selection are filled with the corresponding element, if present (not null), from the specified selection. (If the other selection has additional groups or parents, they are ignored.) This method is used internally by selection.join to merge the enter and update selections after binding data. You can also merge explicitly, although note that since merging is based on element index, you should use operations that preserve index, such as selection.select instead of selection.filter.

note that the *data* is specified for ***each group*** in the selection. if the selection has multiple groups, such as d3.selectAll followed by selection.selectAll, then *data* should be typically be specified as a function. this function will be evaluated for each group in order, being passed the group's *parent* datum, the group index, and the selection's parent nodes, with *this* as the group's parent element
```js
const matrix = [
    [1, 2 ,3],
    [4, 5, 6],
    [7, 8, 9]
]

d3.select('body')
.append('table')
.selectAll('tr')//empty selection
.data(matrix)//assign data to empty selection
.join('tr')//for each extra data, create a tr and assign the datum to it
.attr('id', (d, i) => i)//set the id for each tr based on index
//for each tr selection, select all the descending td.
//now the selection is grouped, [[tr -> [tds...]], [tr -> [tds...]]], and tds is empty
.selectAll('td')
//the selection is grouped, the data should typically be specifed as a function
//for the data function, 
//      the d is the group's parent datum
//      the i is the group's index
//      the nodes is the selection's parent nodes
//if use normal function instead of arrow function, the this refer to the group's parent element
.data((d, i, nodes) => {
    let node = nodes[i]
    //output
    //[1, 2, 3], 0, '0', 'TR'
    //[4, 5, 6], 1, '1', 'TR'
    //[7, 8, 9], 2, '2', 'TR'
    console.log(d, i, node.id, node.tagName)
    return d
})
.join('td')
.text(d => d)
```
if the data is not specified as function for grouped selection, should set data array explicitly. Since selection.selectAll will not pass the datum to new selection automatically

for most method that update nodes properties, d3 pass the data, index, and the groups nodes
```js
//arrow function
d3.selectAll('p').select('b').text((d, i, nodes) => {
    //the d is associated data
    //the i is the index of current element in selection
    //the nodes is current selected group nodes
    console.log(d, i, nodes)
    d3.select(nodes[i]).text()//return current node's text
})

//normal function, could use 'this' directly
d3.selectAll('p').select('b').text(function(d, i) {
    console.log(d, i)
    d3.select(this).text()//return current node's text
})
```

```js
//relevant version
var rects = d3.select('svg')
    .selectAll('rect')
    .data(data)

var newrects = rects.enter()
    .append('rect')
    .style('fill', 'red')//only new created rect fill with red, existed rect keep its color

rects.merge(newrects)
    .attr('width', d => d.value)
    .attr('height', 20)
    .attr('y', (d, i) => i*20)

rects.exit()
    .remove()
```

```js
//the join version
d3.select('svg')
    .selectAll('rect')
    .data(data)
    .join('rect')
    .style('fill', 'red')//all rect fill with red,no matter it exsit already or not
    .attr('width', d => d.value)
    .attr('height', 20)
    .attr('y', (d, i) => i*20)

//join will return the merged selection of enter and update selection
svg.selectAll("circle")
  .data(data)
  .join("circle")
    .attr("fill", "none")//in this way, for both existed circle and new created circle, the 'fill' is changed to 'none'
    .attr("stroke", "black");

//the enter function for join could be specified as a string shorthand as above, its equals to
svg.selectAll("circle")
  .data(data)
  .join(
    enter => enter.append("circle"),//set enter/update/exit explicitly
    update => update,
    exit => exit.remove()
  )
    .attr("fill", "none")
    .attr("stroke", "black");

//By passing separate functions on enter, update and exit, you have greater control over what happens.
svg.selectAll("circle")
  .data(data)
  .join(
    enter => enter.append("circle").attr("fill", "green"),
    update => update.attr("fill", "blue")
  )
    .attr("stroke", "black");
```

# Handle Events
For interaction, selections allow listening for and dispatching of events.

## selection.on(typenames[,listener[,options]])
Adds or removes a listener to each selected element for the specified event typenames. The typenames is a string event type, such as click, mouseover, or submit. 

The type may be optionally followed by a period (.) and a name; the optional name allows multiple callbacks to be registered to receive events of the same type, such as click.foo and click.bar. 

To specify multiple typenames, separate typenames with spaces, such as input change or click.foo click.bar.

If an event listener was previously registered for the same typename on a selected element, the old listener is removed before the new listener is added. To remove a listener, pass null as the listener. To remove all listeners for a given name, pass null as the listener and .foo as the typename, where foo is the name; to remove all listeners with no name, specify . as the typename.

## selection.dispatch(type[,parameters])
Dispatches a custom event of the specified type to each selected element, in order.

## d3.pointer(event[,target])
Returns a two-element array of numbers [x, y] representing the coordinates of the specified event relative to the specified target.

# Control Flow
For advanced usage, selections provide methods for custom control flow.

## selection.each(function)
Invokes the specified function for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element (nodes[i]).

This method can be used to invoke arbitrary code for each selected element, and is useful for creating a context to access parent and child data simultaneously, such as:
```js
parent.each(function(p, j) {
  d3.select(this)
    .selectAll(".child")
      .text(d => `child ${d.name} of ${p.name}`);
});
```

## selection.call(function[,arguments...])
Invokes the specified function exactly once, passing in this selection along with any optional arguments. Returns this selection. 

This is equivalent to invoking the function by hand but facilitates method chaining. For example, to set several styles in a reusable function:
```js
function name(selection, first, last) {
  selection
      .attr("first-name", first)
      .attr("last-name", last);
}

d3.selectAll('div').call(name, "John", "Snow")
//equals to
name(d3.selectAll("div"), "John", "Snow")
```
The only difference is that selection.call always returns the selection and not the return value of the called function

## selection.empty()
return true if this selection contains no non-null elements

## selection.nodes()
Returns an array of all (non-null) elements in this selection

## selection.node()
Returns the first (non-null) element in this selection. If the selection is empty, returns null.

## selection.size()
Returns the total number of (non-null) elements in this selection.
