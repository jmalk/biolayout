grammar gml;

list     : (keyValue)*;
keyValue : KEY value;
KEY      : [a-zA-Z][a-zA-Z0-9]*;
value    : FLOAT
         | INT
         | STRING
         | '[' list ']'
         ;
STRING   : '"' (~[&""]* | '&'[a-zA-Z0-9]+';') '"';
INT      : [0-9]+;
FLOAT    : [0-9]* '.' [0-9]+;
WS       : [ \t\r\n]+ -> skip;
