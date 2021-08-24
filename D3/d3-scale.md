Scales are a convenient abstraction for a fundamental task in visualization: mapping a dimension of abstract data to a visual representation.

# Continuous Scales
## linear scale
Continuous scales map a continuous, quantitative input domain to a continuous output range.

the default value for *domain* and *range* is [0, 1]
```js
var x = d3.scaleLinear()//map [10, 130] to [0, 960] linearly
    .domain([10, 130])
    .range([0, 960]);

//shorthand
var x = d3.scaleLinear([10, 90], [0, 960])

x(20); // 80
x(50); // 320
```
when range is numeric too, we can find the domain value through range value by invert 
```js
x.invert(80)//20
x.invert(320)//50
```
this is convenient when you map your domain to screen coordinate, then you can invert the mouse position to your domain through *invert* easily

what if pass a value outside the domain? it will return a value outside the *range* through extrapolation
```js
x(-10)//-160
x.invert(-160)//-10

//set clamp = true to ensure the return value is always within scale's range
x.clamp(true)
x(-10)//0
x.invert(-160)//10
```

or to apply a color encoding
```js
var color = d3.scaleLinear()
    .domain([10, 100])
    .range(["brown", "steelblue"]);
//shorthand
var color = d3.scaleLinear([10, 100], ["brown", "steelblue"])

color(20); // "#9a3439"
color(50); // "#7b5167"
```
the *domain* could have 2 or more elements, and each element shoud be number, if it have more than 2 elements, it's value must be sorted
```js
var color = d3.scaleLinear()
    .domain([-1, 0, 1])
    .range(["red", "white", "green"]);

color(-0.5); // "rgb(255, 128, 128)"
color(+0.5); // "rgb(128, 192, 128)"
```

## ordinal scale
the domain need not to be numberic, it can be ordinal, and their *range* can be a set of colors, shapes, words...
```js
let lifeSpan = d3.scaleOrdinal()
  .domain(["cat", "rabbit", "dog"])
  .range([16, 2, 13])

lifeSpan("cat")//16
lifeSpan("dog")//13
```
in some case, we does not know the domain, we just got the range
```js
let colors = d3.scaleOrdinal().range(['red', 'blue', 'yellow'])
//shorthand
colors = d3.scaleOrdinal(['red', 'blue', 'yellow'])

colors(1)//red, if the parameter is unkown, d3 will add this parameter to the scale domain, and the ordinal scale cycles on its range to select a new output
colors("2")//blue
colors(1)//red
colors(3)//yellow
colors(4)//red
```

## sqrt scale
sometimes, the relationship between a value and its visual variable associated to it is not always linear.

for example, we want to use colored circle to represent the certain quantity of a country population, the domain is mapped to the circle surface area actually.

but to draw the circlr, we need its radius. to map the surface area to radius, A = PI * R^2, we should use sqrt scale
```js
population2radius = d3.scaleSqrt().domain([0, 2e9]).range([0, 300])

population2radius(1.386e9)//249.73, China's 1.386 billion citizens are represented by a circle of radius =250px
```

# Time scales
Time scales are a variant of linear scales that have a temporal domain: domain values are coerced to dates rather than numbers, and invert likewise returns a date.

the default value for *domain* is [2000-01-01, 2000-01-02], for *range* is [0, 1]
```js
var x = d3.scaleTime()
    .domain([new Date(2000, 0, 1), new Date(2000, 0, 2)])
    .range([0, 960]);

x(new Date(2000, 0, 1,  5)); // 200
x(new Date(2000, 0, 1, 16)); // 640
x.invert(200); // Sat Jan 01 2000 05:00:00 GMT-0800 (PST)
x.invert(640); // Sat Jan 01 2000 16:00:00 GMT-0800 (PST)
```
the ticks for time, the *ticks* return representative dates from the scale's *domain*
```js
var x = d3.scaleTime();

x.ticks(10);
// [Sat Jan 01 2000 00:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 03:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 06:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 09:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 12:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 15:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 18:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 21:00:00 GMT-0800 (PST),
//  Sun Jan 02 2000 00:00:00 GMT-0800 (PST)]
```
we can still specify the interval
```js
var x = d3.scaleTime()
    .domain([new Date(2000, 0, 1, 0), new Date(2000, 0, 1, 2)]);

x.ticks(d3.timeMinute.every(15));
// [Sat Jan 01 2000 00:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 00:15:00 GMT-0800 (PST),
//  Sat Jan 01 2000 00:30:00 GMT-0800 (PST),
//  Sat Jan 01 2000 00:45:00 GMT-0800 (PST),
//  Sat Jan 01 2000 01:00:00 GMT-0800 (PST),
//  Sat Jan 01 2000 01:15:00 GMT-0800 (PST),
//  Sat Jan 01 2000 01:30:00 GMT-0800 (PST),
//  Sat Jan 01 2000 01:45:00 GMT-0800 (PST),
//  Sat Jan 01 2000 02:00:00 GMT-0800 (PST)]
```
also, we can pass a filter function to create ticks
```js
x.ticks(d3.timeMinute.filter(function(d) {
  return d.getMinutes() % 15 === 0;
}));
```

# Band scales
Band scales are like ordinal scales except the output range is continuous and numeric. Discrete output values are automatically computed by the scale by dividing the continuous range into uniform bands. Band scales are typically used for bar charts with an ordinal or categorical dimension. The unknown value of a band scale is effectively undefined: they do not allow implicit domain construction

default value for *domain* is empty, for *range* is [0, 1]

scaleBand will divide the range evenly between the elements of the domain
```js
four = d3
  .scaleBand()
  .domain(["one", "two", "three", "four"])
  .range([0, 100])

four("one")//0, return the start of the band
four("two")//25
four("three")//50
four("four")//75

four.bandwidth()//25, return the band width, and all bands have same bandwidth by band scale definition

four.step()//25, step represent the interval between the start of a band and the start of the next band

//we can adjust the size of the gap between each band, by setting band.paddingInner(fraction)
four.paddingInner(0.2)//the fraction is [0, 1], and its a fraction of the 'step'
//assume the bandscale range = width, and has N elements, then (N - 1) * step + (1- faction) * step = width => step = width / (N - fraction)
four.step()//26.315
four.bandwidth()//21.053

//we can still adjust the padding outside
four.paddingInner(0)//when padding inner equals to 0, then bandwidth = step
    .paddingOuter(0.1)//fraction * step * 2 + N * step = width => step = width / (N + 2 * fraction)

four.step() == four.bandwidth()//23.809, 100/ (4 + 0.2)

//sometimes, we want to config the distribution of outer padding, for example, to shift the bands to one side
four.align(0.5)//the fraction indicate how outer padding is distributed in the range, 0.5 means outer padding should be equally distributed before the first band and after the last band
four.align(0.2)//means the left outer padding only occupy 20% outer padding

//band rounding
//sometimes, the range can not be divided evenly, then we can round the band scale to make the results as integer value
//this will usually leave some unused space, that must be allocated to the left and right outer padding, even if those have been set to 0
testround = d3.scaleBand()
  .domain([1, 2, 3, 4, 5, 6])
  .range([0, 100])
  .round(true)

testround.bandwith()//16
testround(1)//2
```

# Point scales
Point scales are a variant of band scales with the bandwidth fixed to zero. Point scales are typically used for scatterplots with an ordinal or categorical dimension. The unknown value of a point scale is always undefined: they do not allow implicit domain construction.

there is no bandwidth, just step, which means the inner padding is always 1
```js
let point = d3.scalePoint([[domain,] range])//Constructs a new point scale with the specified domain and range, no padding, no rounding and center alignment. 
//If domain is not specified, it defaults to the empty domain. If range is not specified, it defaults to the unit range [0, 1].

point.bandwidth()//always 0
```
