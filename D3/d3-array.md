JS built-in array methods

Mutation methods:
* pop, remove the last element and return
* push, add to the end of array
* shift, remove the first element and return
* unshift, add to the front of array
* splice(startPos, deleteCount, itemsToAdd), return the array of removed elements
* reverse, reverse the order
* sort, sort the elements
* concat, join the array with other array or values
* join(separator), join all elements of the array into a string
* slice(start, end), soft copy the elements from original array to new created array, [start, end)

Iteration methods:
* filter, return a new array which contain the elements that pass the filter
* forEach, call function for each element
* every, to check if all elements in the array meet the test
* some, to check if any element in the array meet the test
* map
* reduce, left-to-right
* reduceRight, right-to-left

D3 array methods
* min(iterable), ignore undefined,NaN,null values, return minimum value in nature order instead of numeric order
* minIndex(iterable), return the index of min value
* max(iterable), 
* maxIndex(iterable)
* extent(iterable), return the array of [min, max]
* sum(iterable),
* mean()
* median()
* quantile(iterable, p), return the p=quantile of the given numbers
* group(iterable, ...key), group the data to Map by key, such as d3.group(datas, d => d.name)
* index(iterable, ...key), similar with *group*, but return a unique value per key instead of an array, and throwing if the key is not unique
* count(iterable[, accessor]), return the count of valid element(not undefined, null, NaN), such as d3.count(datas, d => d.age)
* range([start,] stop[,step]), if start is omitted, default is 0. if step is omitted, default is 1. the stop value is exclusive.
* transpose(matrix), Uses the zip operator as a two-dimensional matrix transpose.
* zip(arrays), Returns an array of arrays, where the ith array contains the ith element from each of the argument arrays. The returned array is truncated in length to the shortest array in arrays. If arrays contains only a single array, the returned array contains one-element arrays. With no arguments, the returned array is empty.


D3 iteration methods

These are equivalent to built-in array methods, but work with any iterable including Map, Set, and Generator.
* every(iterable, test)
* some
* map
* filter
* reduce
* sort
* reverse

D3 Sets methods
* difference(iterable, ...others), return a new Set containing every value in *iterable* and not in any of *others* iterables
* union(...iterables), return a new Set containing every distinct value that apperas in any of the given iterable
* intersection(...iterables), return a new Set containing every distinct value that appears in all of the given iterable
* superset(a, b), return true if a is superset of b
* subset(a, b), return true if a is subset of b