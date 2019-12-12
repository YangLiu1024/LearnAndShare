# ANTLR Introduction

## Grammar structure
```antlr

grammar Name;//the file name should be same as Name,for example, Name.g4, required
parser grammar Name; //only allow parser rule definition
lexer grammar Name; //only allow lexer rule definition

//options and import and tokens at most exist one
options {...}
import ...;
tokens {...} //format: tokens {TOKEN1, TOKEN2, ..., TOKENN}, usually used by actions

//lexer grammar mode only, optional
channels {
  WHITESPACE_CHANNEL,
  COMMENTS_CHANNEL
} 
//usgae
WS : [\r\t\n]+ -> channel(WHITESPACE_CHANNEL);

@actionName {...} //optional

//parser or lexer rules, at least exist one rule
//parser rule name start with lowercase letter
//lexer rule name start with capital letter
rule1
...
ruleN

```

## Parser rules
