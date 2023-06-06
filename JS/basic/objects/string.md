Introduction to string

1. length, the prop of string
2. indexOf(subText, optional: startIndex) => search from startIndex, from beginning to end, return -1 if not exist
3. lastIndexOf(subText, optional: endIndex) => search from endIndex, from endIndex to beginning, return -1 if not exist
4. search(regex), return -1 if not exist.
   match(regex), return all the matched text as array or null if no match
5. slice(startIndex, optional: endIndex). for example, "ABCD".slice(1,3) => "BC", "ABCD".slice(-3, -1) => "BC", "ABCD".slice(-2) => "CD"
  - if startIndex positive, counting from beginning, extract text between [startIndex, endIndex)
  - if startIndex negative, counting from ending, extract text between (startIndex, endIndex]
6. substring(startIndex, endIndex), similiar to slice, but does not support negative index
7. substr(startIndex,length), similiar to slice, but the second parameter stand for the length
8. replace(regex, replacedText), for example, "ABCDABCD".replace(/BC/i, "CB")(ignore case), "ABCDABCD".replace(/BC/g, "CB")(repace all match)
9. toUpperCase()
10. toLowerCase()
11. concat(str), equal to "+"
12. trim()
13. charAt(index)
14. charCodeAt(index), return UTF-16 code of char
15. access by []. note that it act like array, but its not. str[index] is only readable, str[index] = "A" does not work, and give no error
16. split(text). split the string to array. for example, "ABBC".split() => ["ABBC"], "ABBC".split("") => ["A", "B", "B", "C"], "ABBC".split("B") => ["A", "", "C"], "ABBC".split("BB" => ["A", "C"])
  - if omit parameter, the return array has only one element: the original string
  - if pass empty text, the return array will be array of all single char
17. startsWith(text)
18. endsWith(text)
19. includes(text)

Note that all string method will not modify original text.
