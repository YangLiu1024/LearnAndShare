Introduction to array

Array properties and method
1. length, return array size
2. forEach((v, i) => {})
3. push(v), push value to array end
4. arr[index] = val, if index out of range, will introduce undefined holes
5. toString(), join array elements with comma
6. join(separator), join array elements with separtor
7. pop(), remove last element and return. if empty, return undefined
8. shift(), remove first element and return.
9. unshift(v), add value to array beginning, and return array length
10. delete arr[index], the arr[index] will be undefined. to remove the hole, better to use pop() or shift()
11. splice(startIndex, removeLength, ...addedValues). from startIndex. remove specified size "removeLength" elements, then append addValues from startIndex. return the array of removed elements
12. concat(arr)
13. slice(startIndex, endIndex), slices out a piece of an array into a new array. [startIndex, endIndex)
13. every(function(val, idx, arr), thisValue), check if all element pass the test
14. filter(function(val, idx, arr), thisValue), creates an array filled with all array elements that pass a test
15. find(function(val, idx, arr), thisValue), return the first value which pass the test
16. includes(val), check if arr contain val
17. indexOf(val, startIndex), return the index of val, if not exist, return -1. always search to end. if startIndex negative, counting from end
18. lastIndexOf(val, startIndex), always search to beginning, if stardIndex negative, counting from end
19. map(function(val, idx, arr), thisValue), creates a new array with the results of calling a function for every array element
20. reduce(function(total, currentValue, currentIndex, arr), initValue).
for example, var arr = ["a", "b", "c"]; arr.reduce((result, val, idx) => result += val, "") => "abc"
    reduceRight(), fimiliar with reduce, just traverse from right to left
21. reverse(), reverse the order of original arr, this method will change original array
22. some(function(val, idx, arr), thisValue), check if any element in array pass the test
23. sort(function(a,b){return positive value or 0 or negative value}).this method change original array

How to check a variable is array or not
1. Array.isArray(v)
2. v instanceof Array

How to find max or min value of array
1. Math.max(arr)
2. Math.min(arr)
