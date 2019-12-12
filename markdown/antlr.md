# ANTLR Introduction

## Grammar structure
```antlr

grammar Name;//the file name should be same as Name,for example, Name.g4, required
//options and import and tokens at most exist one
options {...}
import ...;
tokens {...}

channels {...} //leser only, optional
@actionName {...} //optional

rule1 //parser or lexer rules, at least exist one rule
...
ruleN

```
