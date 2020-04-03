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
5. js comparison. note that when compare number and string, always convert string to number. empty text => 0, non-number text convert to NaN. when compare with NaN, always return false. when compare two objects, always return false, because they are different object.
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
12. js array.splice(-1, 1) will delete element from ending
13. string in numeric operation will convert to number auto, for example, "100" - "10" => 90
14. NaN is a number, typeof NaN => "number". but all logic operations with NaN will return false, even NaN == NaN. to check if a value is NaN, call global function isNaN(val)
15. Infinity is a number, typeof Infinity => "number". the value outside of possible range will be Infinity or -Infinity
16. the difference between array and object
    - array use numbered index, object use named index
    - array is a special kind of object
17. Math.PI, Math.round(), Math.ceil(), Math.floor(), Math.pow(x, y), Math.abs(), Match.sqrt(), Math.sin(), Math.cos(), Math.max(),
  Math.min(), Math.random() return [0, 1)
18. Boolean(var) => true when var has value, false when var has not value.
  0, "", -0, null, undefined, NaN, false => their Boolean(v) is false
19. if a variable is used without declared, regard as declared as var
20. switch statement use strict match ===, and need to add break for each case
21. js loop
    - for (statement1; statement2; statement3), general for sentence
    - for key in object, loops through the keys of an iterable objects
    - for value of object, loops through the values of an iterable objects
    - while and do/while
22. typeof always return string
23. js regex modifiers
  - i, ignore case
  - g, find all match instead of first
24. regex object methods
  - test, for example, /e/.test("abcdef") => true, because the text contain e
  - exec, return the matched text as object
25. try {} catch (err) {} finally {}. the err thrown by js itself always has properties "name" and "message"
26. user can throw customized error. it could be string, number, boolean, object. and the catch will catch what you throw
