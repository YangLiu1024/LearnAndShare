Panning and zooming are popular interaction techniques which let the user focus on a region of interest by restricting the view. 

It is easy to learn due to direct manipulation: click-and-drag to pan (translate), spin the wheel to zoom (scale), or use touch. 

Panning and zooming are widely used in web-based mapping, but can also be used with visualizations such as time-series and scatterplots.

The zoom behavior implemented by d3-zoom is a convenient but flexible abstraction for enabling pan-and-zoom on selections. It handles a surprising variety of input events and browser quirks. The zoom behavior is agnostic about the DOM, so you can use it with SVG, HTML or Canvas.

# Zoom API

## d3.zoom()
Creates a new zoom behavior. The returned behavior, zoom, is both an object and a function, and is typically applied to selected elements via selection.call.

## zoom(selection)
Applies this zoom behavior to the specified selection, binding the necessary event listeners to allow panning and zooming, and initializing the zoom transform on each selected element to the identity transform if not already defined.

This function is typically not invoked directly, and is instead invoked via selection.call. For example, to instantiate a zoom behavior and apply it to a selection:
```js
selection.call(d3.zoom().on("zoom", zoomed));
```
Internally, the zoom behavior uses selection.on to bind the necessary event listeners for zooming. The listeners use the name *.zoom*, so you can subsequently unbind the zoom behavior as follows:
```js
selection.on(".zoom", null);
```
To disable just wheel-driven zooming (say to not interfere with native scrolling), you can remove the zoom behavior’s wheel event listener after applying the zoom behavior to the selection:
```js
selection
    .call(zoom)
    .on("wheel.zoom", null);
```
## zoom.transform(selection, transform[,point])
If selection is a selection, sets the current zoom transform of the selected elements to the specified transform, instantaneously emitting start, zoom and end events.

If selection is a transition, defines a “zoom” tween to the specified transform using d3.interpolateZoom, emitting a start event when the transition starts, zoom events for each tick of the transition, and then an end event when the transition ends

The transition will attempt to minimize the visual movement around the specified point; if the point is not specified, it defaults to the center of the viewport extent.

This function is typically not invoked directly, and is instead invoked via selection.call or transition.call. For example, to reset the zoom transform to the identity transform instantaneously:
```js
selection.call(zoom.transform, d3.zoomIdentity);
```
To smoothly reset the zoom transform to the identity transform over 750 milliseconds:
```js
selection.transition().duration(750).call(zoom.transform, d3.zoomIdentity);
```

## zoom.translateBy(selection, x, y)
If selection is a selection, translates the current zoom transform of the selected elements by x and y, such that the new tx1 = tx0 + kx and ty1 = ty0 + ky. 

If selection is a transition, defines a “zoom” tween translating the current transform. This method is a convenience method for zoom.transform. 

The x and y translation amounts may be specified either as numbers or as functions that return numbers. If a function, it is invoked for each selected element, being passed the current datum d and index i, with the this context as the current DOM element.

## zoom.translateTo(selection, x, y[,p])
If selection is a selection, translates the current zoom transform of the selected elements such that the given position ⟨x,y⟩ appears at given point p. The new tx = px - kx and ty = py - ky. If p is not specified, it defaults to the center of the viewport extent. If selection is a transition, defines a “zoom” tween translating the current transform. This method is a convenience method for zoom.transform. The x and y coordinates may be specified either as numbers or as functions that returns numbers; similarly the p point may be specified either as a two-element array [px,py] or a function. If a function, it is invoked for each selected element, being passed the current datum d and index i, with the this context as the current DOM element.

## zoom.scaleBy(selection, k[,p])
If selection is a selection, scales the current zoom transform of the selected elements by k, such that the new k₁ = k₀k. The reference point p does move. If p is not specified, it defaults to the center of the viewport extent. If selection is a transition, defines a “zoom” tween translating the current transform. This method is a convenience method for zoom.transform. The k scale factor may be specified either as a number or a function that returns a number; similarly the p point may be specified either as a two-element array [px,py] or a function. If a function, it is invoked for each selected element, being passed the current datum d and index i, with the this context as the current DOM element.

## zoom.scaleTo(selection, k[,p])
If selection is a selection, scales the current zoom transform of the selected elements to k, such that the new k₁ = k.

## zoom.filter([filter])
If filter is specified, sets the filter to the specified function and returns the zoom behavior. If filter is not specified, returns the current filter, which defaults to:
```js
function filter(event) {
  return (!event.ctrlKey || event.type === 'wheel') && !event.button;
}
```
he filter is passed the current event (event) and datum d, with the this context as the current DOM element. If the filter returns falsey, the initiating event is ignored and no zoom gestures are started.

Thus, the filter determines which input events are ignored. The default filter ignores mousedown events on secondary buttons, since those buttons are typically intended for other purposes, such as the context menu.

## zoom.extent([extent])
If extent is specified, sets the viewport extent to the specified array of points [[x0, y0], [x1, y1]], where [x0, y0] is the top-left corner of the viewport and [x1, y1] is the bottom-right corner of the viewport, and returns this zoom behavior. The extent may also be specified as a function which returns such an array; if a function, it is invoked for each selected element, being passed the current datum d, with the this context as the current DOM element.

If extent is not specified, returns the current extent accessor, which defaults to [[0, 0], [width, height]] where width is the client width of the element and height is its client height; for SVG elements, the nearest ancestor SVG element’s viewBox, or width and height attributes, are used. Alternatively, consider using element.getBoundingClientRect.

## zoom.scaleExtent([extent])
If extent is specified, sets the scale extent to the specified array of numbers [k0, k1] where k0 is the minimum allowed scale factor and k1 is the maximum allowed scale factor, and returns this zoom behavior.

If extent is not specified, returns the current scale extent, which defaults to [0, ∞].

The scale extent restricts zooming in and out. It is enforced on interaction and when using zoom.scaleBy, zoom.scaleTo and zoom.translateBy; however, it is not enforced when using zoom.transform to set the transform explicitly.

If the user tries to zoom by wheeling when already at the corresponding limit of the scale extent, the wheel events will be ignored and not initiate a zoom gesture. This allows the user to scroll down past a zoomable area after zooming in, or to scroll up after zooming out. If you would prefer to always prevent scrolling on wheel input regardless of the scale extent, register a wheel event listener to prevent the browser default behavior:
```js
selection
    .call(zoom)
    .on("wheel", event => event.preventDefault());
```

## zoom.traslateExtent([extent])
If extent is specified, sets the translate extent to the specified array of points [[x0, y0], [x1, y1]], where [x0, y0] is the top-left corner of the world and [x1, y1] is the bottom-right corner of the world, and returns this zoom behavior. If extent is not specified, returns the current translate extent, which defaults to [[-∞, -∞], [+∞, +∞]]. The translate extent restricts panning, and may cause translation on zoom out. It is enforced on interaction and when using zoom.scaleBy, zoom.scaleTo and zoom.translateBy; however, it is not enforced when using zoom.transform to set the transform explicitly.

## zoom.duration([duration])
If duration is specified, sets the duration for zoom transitions on double-click and double-tap to the specified number of milliseconds and returns the zoom behavior. If duration is not specified, returns the current duration, which defaults to 250 milliseconds. If the duration is not greater than zero, double-click and -tap trigger instantaneous changes to the zoom transform rather than initiating smooth transitions.

o disable double-click and double-tap transitions, you can remove the zoom behavior’s dblclick event listener after applying the zoom behavior to the selection:
```js
selection
    .call(zoom)
    .on("dblclick.zoom", null);
```
## zoom.on(typenames[,listener])
If listener is specified, sets the event listener for the specified typenames and returns the zoom behavior. If an event listener was already registered for the same type and name, the existing listener is removed before the new listener is added. If listener is null, removes the current event listeners for the specified typenames, if any. If listener is not specified, returns the first currently-assigned listener matching the specified typenames, if any. When a specified event is dispatched, each listener will be invoked with the same context and arguments as selection.on listeners: the current event (event) and datum d, with the this context as the current DOM element.

The typenames is a string containing one or more typename separated by whitespace. Each typename is a type, optionally followed by a period (.) and a name, such as zoom.foo and zoom.bar; the name allows multiple listeners to be registered for the same type. The type must be one of the following:
* start, after zooming begins (such as on mousedown).
* zoom, after a change to the zoom transform (such as on mousemove)
* end, after zooming ends (such as on mouseup ).

# Zoom Events
When a zoom event listener is invoked, it receives the current zoom event as a first argument. The event object exposes several fields:
* event.target - the associated zoom behavior.
* event.type - the string “start”, “zoom” or “end”; see zoom.on.
* event.transform - the current zoom transform.
* event.sourceEvent - the underlying input event, such as mousemove or touchmove.

# Zoom Transform
The zoom behavior stores the zoom state on the element to which the zoom behavior was applied, not on the zoom behavior itself. This is because the zoom behavior can be applied to many elements simultaneously, and each element can be zoomed independently. 

The zoom state can change either on user interaction or programmatically via zoom.transform.

To retrieve the zoom state, use event.transform on the current zoom event within a zoom event listener (see zoom.on), or use d3.zoomTransform for a given node. 

The latter is particularly useful for modifying the zoom state programmatically, say to implement buttons for zooming in and out.

## d3.zoomTansform(node)
Returns the current transform for the specified node. Note that node should typically be a DOM element, not a selection. (A selection may consist of multiple nodes, in different states, and this function only returns a single transform.) If you have a selection, call selection.node first:
```js
var transform = d3.zoomTransform(selection.node());
```
In the context of an event listener, the node is typically the element that received the input event
```js
var transform = d3.zoomTransform(this)

//equals to
event.transform
```
Internally, an element’s transform is stored as element.__zoom; however, you should use this method rather than accessing it directly. If the given node has no defined transform, returns the transform of the closest ancestor, or if none exists, the identity transformation. The returned transform represents a two-dimensional transformation matrix of the form:

k 0 tx
0 k ty
0 0 1

(This matrix is capable of representing only scale and translation; a future release may also allow rotation, though this would probably not be a backwards-compatible change.) The position ⟨x,y⟩ is transformed to ⟨xk + tx,yk + ty⟩. The transform object exposes the following properties:
* transform.x - the translation amount tx along the x-axis.
* transform.y - the translation amount ty along the y-axis.
* transform.k - the scale factor k

These properties should be considered read-only; instead of mutating a transform, use transform.scale and transform.translate to derive a new transform. Also see zoom.scaleBy, zoom.scaleTo and zoom.translateBy for convenience methods on the zoom behavior. 

To create a transform with a given k, tx, and ty:
```js
var t = d3.zoomIdentity.translate(x, y).scale(k);
```
Similarly, to apply the transformation to HTML elements via CSS:
```js
div.style("transform", "translate(" + transform.x + "px," + transform.y + "px) scale(" + transform.k + ")");
div.style("transform-origin", "0 0");
```
To apply the transformation to SVG:
```js
g.attr("transform", "translate(" + transform.x + "," + transform.y + ") scale(" + transform.k + ")");
```
Or more simply, taking advantage of transform.toString:
```js
g.attr("transform", transform);
```
Note that the order of transformations matters! The translate must be applied before the scale.

## transform.toString()
return a string representing the SVG transform corresponding to this transform
```js
function toString() {
  return "translate(" + this.x + "," + this.y + ") scale(" + this.k + ")";
}
```

## transform.rescaleX(x)
Returns a copy of the continuous scale x whose domain is transformed. This is implemented by first applying the inverse x-transform on the scale’s range, and then applying the inverse scale to compute the corresponding domain:
```js
function rescaleX(x) {
  //设想一个图含有x,y 轴，当 zoom chart 时，我们想让 x,y 轴的坐标也跟着 zoom
  //所以这个时候调用 transform.rescaleX(x)， 就会计算在当前 zoom 条件下的 x scale
  //它会改变 scale 的 domain，但是不会改变 range
  var range = x.range().map(transform.invertX, transform),
      domain = range.map(x.invert, x);
  return x.copy().domain(domain);
}
```
## transform.rescaleY(y)
Similar with transform.rescaleX(x)
## d3.zoomIdentity
The identity transform, where k = 1, tx = ty = 0.

