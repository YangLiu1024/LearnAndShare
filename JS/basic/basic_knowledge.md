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
