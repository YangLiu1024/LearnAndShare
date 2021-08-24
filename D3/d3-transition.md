A transition is a selection-like interface for animating changes to the DOM. Instead of applying changes instantaneously, transitions smoothly interpolate the DOM from its current state to the desired target state over a given duration.

To apply a transition, select elements, call selection.transition, and then make the desired changes. For example:
```js
d3.select("body")
  .transition()
    .style("background-color", "red");
```
Transitions support most selection methods (such as transition.attr and transition.style in place of selection.attr and selection.style), but not all methods are supported; for example, you must append elements or bind data before a transition starts. A transition.remove operator is provided for convenient removal of elements when the transition ends.

To compute intermediate state, transitions leverage a variety of built-in interpolators. Colors, numbers, and transforms are automatically detected. Strings with embedded numbers are also detected, as is common with many styles (such as padding or font sizes) and paths. To specify a custom interpolator, use transition.attrTween, transition.styleTween or transition.tween.

# Selecting Elements
Transitions are derived from selections via selection.transition. You can also create a transition on the document root element using d3.transition.

## selection.transition([name])
Returns a new transition on the given selection with the specified name. If a name is not specified, null is used. The new transition is only exclusive with other transitions of the same name.

If the name is a transition instance, the returned transition has the same id and name as the specified transition. If a transition with the same id already exists on a selected element, the existing transition is returned for that element. Otherwise, the timing of the returned transition is inherited from the existing transition of the same id on the nearest ancestor of each selected element. Thus, this method can be used to synchronize a transition across multiple selections, or to re-select a transition for specific elements and modify its configuration. For example:
```js
var t = d3.transition()
    .duration(750)
    .ease(d3.easeLinear);
//first, check if .apple element has transition t already, if yes, return t directly
//if not, try to find the transition with same id on the nearest ancestor for each selected element
//if the transition is not found on a selected node or its ancestor, use default timing parameters
d3.selectAll(".apple").transition(t)
    .style("fill", "red");

d3.selectAll(".orange").transition(t)
    .style("fill", "orange");
```
If the specified transition is not found on a selected node or its ancestors (such as if the transition already ended), the default timing parameters are used; however, in a future release, this will likely be changed to throw an error

## selection.interrupt([name])
Interrupts the active transition of the specified name on the selected elements, and cancels any pending transitions with the specified name, if any. If a name is not specified, null is used.

Interrupting a transition on an element has no effect on any transitions on any descendant elements.

## d3.interrupt(node[,name])
Interrupts the active transition of the specified name on the specified node, and cancels any pending transitions with the specified name, if any. If a name is not specified, null is used. 

## d3.transition([name])
Returns a new transition on the root element, document.documentElement, with the specified name. If a name is not specified, null is used. The new transition is only exclusive with other transitions of the same name. The name may also be a transition instance; This method is equivalent to:
```js
d3.selection()//select the root element
  .transition(name)
```
## transition.selection()
Returns the selection corresponding to this transition.

## transition.select(selector)
For each selected element, selects the first descendant element that matches the specified selector string, if any, and returns a transition on the resulting selection. The selector may be specified either as a selector string or a function. If a function, it is evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. The new transition has the same id, name and timing as this transition; however, if a transition with the same id already exists on a selected element, the existing transition is returned for that element.

This method is equivalent to deriving the selection for this transition via transition.selection, creating a subselection via selection.select, and then creating a new transition via selection.transition:
```js
transition
  .selection()
  .select(selector)
  .transition(transition)
```
## transition.selectAll(selector)
Similar with *transition.select(selector)*, just select all *descendant* elements(instead of first) that match the selector

## transition.selectChild([selector])
Similar with *transition.select(selector)*, just select first *child* element

## transition.selectChildren(selector)
Similar with *transition.selectChild([selector])*, just select all children that match the selector

## transition.filter(filter)
For each selected element, selects only the elements that match the specified filter, and returns a transition on the resulting selection. The filter may be specified either as a selector string or a function. If a function, it is evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. The new transition has the same id, name and timing as this transition; however, if a transition with the same id already exists on a selected element, the existing transition is returned for that element.
```js
transition
  .selection()
  .filter(filter)
  .transition(transition)
```

## transition.merge(other)
Returns a new transition merging this transition with the specified other transition, which must have the same id as this transition.

The returned transition has the same number of groups, the same parents, the same name and the same id as this transition. Any missing (null) elements in this transition are filled with the corresponding element, if present (not null), from the other transition.
```js
transition
  .selection()
  .merge(other.selection())
  .transition(transition)
```

## transition.transition()
Returns a new transition on the same selected elements as this transition, scheduled to start when this transition ends.

The new transition inherits a reference time equal to this transition’s time plus its delay and duration. The new transition also inherits this transition’s name, duration, and easing. This method can be used to schedule a sequence of chained transitions. For example:
```js
d3.selectAll(".apple")
  .transition() // First fade to green.
    .style("fill", "green")
  .transition() // Then red.
    .style("fill", "red")
  .transition() // Wait one second. Then brown, and remove.
    .delay(1000)
    .style("fill", "brown")
    .remove();
```
The delay for each transition is relative to its previous transition. Thus, in the above example, apples will stay red for one second before the last transition to brown starts.

## d3.active(node[,name])
Returns the active transition on the specified node with the specified name, if any. If no name is specified, null is used. Returns null if there is no such active transition on the specified node. This method is useful for creating chained transitions. For example, to initiate disco mode:
```js
d3.selectAll("circle").transition()
    .delay(function(d, i) { return i * 50; })
    .on("start", function repeat() {
        d3.active(this)
            .style("fill", "red")
          .transition()
            .style("fill", "green")
          .transition()
            .style("fill", "blue")
          .transition()
            .on("start", repeat);
      });
```

# Modify Elements
After selecting elements and creating a transition with selection.transition, use the transition’s transformation methods to affect document content.

## transition.attr(name, value)
For each selected element, assigns the attribute tween for the attribute with the specified name to the specified target value. The starting value of the tween is the attribute’s value when the transition starts. The target value may be specified either as a constant or a function. If a function, it is immediately evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element.

If the target value is null, the attribute is removed when the transition starts. Otherwise, an interpolator is chosen based on the type of the target value, using the following algorithm:
1. if value is number, use *interpolateNumber*
2. if value is a color or a string coercible to a color, use *interpolateRgb*
3. use *interpolateString*

To apply a different interpolator, use *transition.attrTween*

## transition.attrTween(name[, factory])
If factory is specified and not null, assigns the attribute tween for the attribute with the specified name to the specified interpolator factory. An interpolator factory is a function that returns an interpolator;

when the transition starts, the factory is evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. The returned interpolator will then be invoked for each frame of the transition, in order, being passed the eased time t, typically in the range [0, 1]. Lastly, the return value of the interpolator will be used to set the attribute value. 

The interpolator must return a string. (To remove an attribute at the start of a transition, use transition.attr; to remove an attribute at the end of a transition, use transition.on to listen for the end event.)

If the specified factory is null, removes the previously-assigned attribute tween of the specified name, if any. If factory is not specified, returns the current interpolator factory for attribute with the specified name, or undefined if no such tween exists.

For example, to interpolate the fill attribute from red to blue:
```js
transition.attrTween("fill", function() {
  return d3.interpolateRgb("red", "blue");//the transition always start from red, end with blue, regardless of current fill attribute value
});
```
Or to interpolate from the current fill to blue, like transition.attr
```js
transition.attrTween("fill", function() {
  return d3.interpolateRgb(this.getAttribute("fill"), "blue");//start from current fill attribute value, end with blue
});
```
Or to apply a custom rainbow interpolator:
```js
transition.attrTween("fill", function(d, i, nodes) {//datum, index, nodes
  return function(t) {//t is eased time, in the range [0, 1]
    return "hsl(" + t * 360 + ",100%,50%)";
  };
});
```
This method is useful to specify a custom interpolator, such as one that understands SVG paths. A useful technique is data interpolation, where d3.interpolateObject is used to interpolate two data values, and the resulting value is then used (say, with a shape) to compute the new attribute value.

## transition.style(name, value[,priority])
For each selected element, assigns the style tween for the style with the specified name to the specified target value with the specified priority. The starting value of the tween is the style’s inline value if present, and otherwise its computed value, when the transition starts. 

The target value may be specified either as a constant or a function. If a function, it is immediately evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element.

If the target value is null, the style is removed when the transition starts. Otherwise, an interpolator is chosen based on the type of the target value

## transition.styleTween
Similar with *transition.attrTween*

## transition.text(value)
For each selected element, sets the text content to the specified target value when the transition starts. The value may be specified either as a constant or a function. If a function, it is immediately evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. The function’s return value is then used to set each element’s text content. A null value will clear the content.

Text is not interpolated by default because it is usually undesirable.

## transition.textTween
If factory is specified and not null, assigns the text tween to the specified interpolator factory. An interpolator factory is a function that returns an interpolator; when the transition starts, the factory is evaluated for each selected element, in order, being passed the current datum d and index i, with the this context as the current DOM element. The returned interpolator will then be invoked for each frame of the transition, in order, being passed the eased time t, typically in the range [0, 1]. Lastly, the return value of the interpolator will be used to set the text. The interpolator must return a string.

## transition.remove()
For each selected element, removes the element when the transition ends, as long as the element has no other active or pending transitions. If the element has other active or pending transitions, does nothing.

## transition.tween(name[, value])
For each selected element, assigns the tween with the specified name with the specified value function. The value must be specified as a function that returns a function. When the transition starts, the value function is evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. The returned function is then invoked for each frame of the transition, in order, being passed the eased time t, typically in the range [0, 1]. If the specified value is null, removes the previously-assigned tween of the specified name, if any.

For example, to interpolate the fill attribute to blue, like transition.attr:
```js
transition.tween("attr.fill", function() {
  var i = d3.interpolateRgb(this.getAttribute("fill"), "blue");
  return function(t) {
    this.setAttribute("fill", i(t));
  };
});
```

# Timing
The easing, delay and duration of a transition is configurable. For example, a per-element delay can be used to stagger the reordering of elements, improving perception. 

## transition.delay([value])
For each selected element, sets the transition delay to the specified value in milliseconds. The value may be specified either as a constant or a function. If a function, it is immediately evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. The function’s return value is then used to set each element’s transition delay. If a delay is not specified, it defaults to zero.

If a value is not specified, returns the current value of the delay for the first (non-null) element in the transition. This is generally useful only if you know that the transition contains exactly one element.

Setting the delay to a multiple of the index i is a convenient way to stagger transitions across a set of elements. For example:
```js
transition.delay(function(d, i) { return i * 10; });
```
Of course, you can also compute the delay as a function of the data, or sort the selection before computed an index-based delay.

# transition.duration([value])
For each selected element, sets the transition duration to the specified value in milliseconds. The value may be specified either as a constant or a function. If a function, it is immediately evaluated for each selected element, in order, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. The function’s return value is then used to set each element’s transition duration. If a duration is not specified, it defaults to 250ms.

If a value is not specified, returns the current value of the duration for the first (non-null) element in the transition. This is generally useful only if you know that the transition contains exactly one element.

## transition.ease([value])
Specifies the transition easing function for all selected elements. The value must be specified as a function. The easing function is invoked for each frame of the animation, being passed the normalized time t in the range [0, 1]; it must then return the eased time tʹ which is typically also in the range [0, 1]. A good easing function should return 0 if t = 0 and 1 if t = 1. If an easing function is not specified, it defaults to d3.easeCubic.

If a value is not specified, returns the current easing function for the first (non-null) element in the transition. This is generally useful only if you know that the transition contains exactly one element.

## transition.easeVarying(factory)
Specifies a factory for the transition easing function. The factory must be a function. It is invoked for each node of the selection, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. It must return an easing function.

# Control Flow
For advanced usage, transitions provide methods for custom control flow

## transition.end()
Returns a promise that resolves when every selected element finishes transitioning. If any element’s transition is cancelled or interrupted, the promise rejects.

## transition.on(typenames[,listener])
Adds or removes a listener to each selected element for the specified event typenames. The typenames is one of the following string event types:
* start - when the transtion start
* end - when the transition ends
* interrupt - when the transition is interrupted
* cancel - when the transition is cancelled

Note that these are not native DOM events as implemented by selection.on and selection.dispatch, but transition events!

The type may be optionally followed by a period (.) and a name; the optional name allows multiple callbacks to be registered to receive events of the same type, such as start.foo and start.bar. To specify multiple typenames, separate typenames with spaces, such as interrupt end or start.foo start.bar.

When a specified transition event is dispatched on a selected node, the specified listener will be invoked for the transitioning element, being passed the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. Listeners always see the latest datum for their element, but the index is a property of the selection and is fixed when the listener is assigned; to update the index, re-assign the listener.

If an event listener was previously registered for the same typename on a selected element, the old listener is removed before the new listener is added. To remove a listener, pass null as the listener. To remove all listeners for a given name, pass null as the listener and .foo as the typename, where foo is the name; to remove all listeners with no name, specify . as the typename.

If a listener is not specified, returns the currently-assigned listener for the specified event typename on the first (non-null) selected element, if any. If multiple typenames are specified, the first matching listener is returned.

## transition.each(function)
Invokes the specified function for each selected element, passing in the current datum (d), the current index (i), and the current group (nodes), with this as the current DOM element. This method can be used to invoke arbitrary code for each selected element, and is useful for creating a context to access parent and child data simultaneously. Equivalent to selection.each.

## transition.call(function[,arguments...])
Invokes the specified function exactly once, passing in this transition along with any optional arguments. Returns this transition. This is equivalent to invoking the function by hand but facilitates method chaining. 

For example, to set several attributes in a reusable function:
```js
function color(transition, fill, stroke) {
  transition
      .style("fill", fill)
      .style("stroke", stroke);
}

d3.selectAll("div").transition().call(color, "red", "blue");

//equivalent to
color(d3.selectAll("div").transition(), "red", "blue");
```

## transition.empty()
Returns true if this transition contains no (non-null) elements. Equivalent to *selection.empty*.

## transition.nodes()
Returns an array of all (non-null) elements in this transition. Equivalent to selection.nodes.

## transition.node()
Returns the first (non-null) element in this transition. If the transition is empty, returns null. Equivalent to selection.node.

## transition.size()
Returns the total number of elements in this transition. Equivalent to selection.size.

# The Life of a Transition
Immediately after creating a transition, such as by selection.transition or transition.transition, you may configure the transition using methods such as transition.delay, transition.duration, transition.attr and transition.style. Methods that specify target values (such as transition.attr) are evaluated synchronously; however, methods that require the starting value for interpolation, such as transition.attrTween and transition.styleTween, must be deferred until the transition starts.

Shortly after creation, either at the end of the current frame or during the next frame, the transition is scheduled. At this point, the delay and start event listeners may no longer be changed; attempting to do so throws an error with the message “too late: already scheduled” (or if the transition has ended, “transition not found”).

When the transition subsequently starts, it interrupts the active transition of the same name on the same element, if any, dispatching an interrupt event to registered listeners. (Note that interrupts happen on start, not creation, and thus even a zero-delay transition will not immediately interrupt the active transition: the old transition is given a final frame. Use selection.interrupt to interrupt immediately.) The starting transition also cancels any pending transitions of the same name on the same element that were created before the starting transition. The transition then dispatches a start event to registered listeners. This is the last moment at which the transition may be modified: the transition’s timing, tweens, and listeners may not be changed when it is running; attempting to do so throws an error with the message “too late: already running” (or if the transition has ended, “transition not found”). The transition initializes its tweens immediately after starting.

During the frame the transition starts, but after all transitions starting this frame have been started, the transition invokes its tweens for the first time. Batching tween initialization, which typically involves reading from the DOM, improves performance by avoiding interleaved DOM reads and writes.

For each frame that a transition is active, it invokes its tweens with an eased t-value ranging from 0 to 1. Within each frame, the transition invokes its tweens in the order they were registered.

When a transition ends, it invokes its tweens a final time with a (non-eased) t-value of 1. It then dispatches an end event to registered listeners. This is the last moment at which the transition may be inspected: after ending, the transition is deleted from the element, and its configuration is destroyed. (A transition’s configuration is also destroyed on interrupt or cancel.) Attempting to inspect a transition after it is destroyed throws an error with the message “transition not found”.