Introduction to basic knowledge of JS

1. <script> could be placed in both <head> and <body> section, and its better to place at bottom of <body> to improve display speed
2. place js code in external javascript file, then reference in html, such as 
  - <script sre="demo.js">
  - <script src="https://my.learn.share.com/demo.js">
  - <script src="/src/demo.js">. note that "/src/demo.js" refer to the root dir of current web
3. built-in methods
  - document.getElementById("id").innerHTML = ""
  - window.alert("")
  - console.log("")
  - window.print() print current window
4. a variable declared without value, will have default value <b>undefined</b>. if re-declared the variable, its value will be kept
5. js comparison. note that when compare number and string, always convert string to number. empty text => 0, non-number text convert to NaN. when compare with NaN, always return false.
  - <b>==</b> equal value
  - <b>===</b> equal value and equal type
  - <b>!=</b> different value
  - <b>!==</b> different value or different type
6. the difference between null and undefined
  - if a variable declared without value, its has default value <b>undefined</b>
  - null could be asigned to a variable as a kind of special value
  - null == undefined => true, null === undefined => false. because typeof null => object, typeof undefined => 'undefined'
7. js data types
  - string
  - number
  - boolean
  - object note that for array, typeof array still return object
  - function
  - undefined
8. js operator precedence, list in descend order
  - () expression grouping
  - . [] () new  member access operator and function call and creator
  - postfix ++ --
  - prefix ++ -- ! typeof 
  - **  Exponentiation
  - \* / %
  - \+ -
  - shift operator >> << >>>
  - <= >= > < instanceof in
  - == === != !==
  - other...
9. access object property by obj.propname or obj["propname"], access object method by obj.func()
10. this in function refer to the function owner
11. js common events in html: onclick, onmouseover, onmouseout, onkeydown, etc...
