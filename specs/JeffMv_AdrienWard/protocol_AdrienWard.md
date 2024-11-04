# ACP (Arithmetic Calcul Protocol) V 1.0

## Overview

Client-server protocol (without state) for mathematics calcul with arithmetic operation.
the client seen prefix arithmetic expression.

## Transport Layer protocol

Uses TCP port 1111, client sends the calculation to the server the server **<u>does not respond</u>** with the answer or an error code if it could not perform the operations.

### structure message client

"head "

**Head** *metadata*
    "version" *version protocol*
    "base_number" *default is 10*
    "format_number" *default is binary*
    "arithmetic expressions"
**Data**
*exemple use prefix notation* 
    "op_multi"
    "nb_a"
    "op_add"
    "nb_b"
    "nb_c"

#### Head

"version" = 1 octect not signed
*version of protocol, actuelly is 1.0*



"base_number" = 1 octect not signed
version 1 supporte *base 2*, *base 10* and *base 16*

2 = base 2
10 = base 10
16 = base 16


"format_number" = 1 octect not signed

1 = byte_signed
2 = short_signed
3 = int_signed
4 = long_signed
5 = float_signed
6 = double_signed

7 = byte_not_signed
8 = short_not_signed
9 = int_not_signed
10 = long_not_signed
11 = float_not_signed
12 = double_not_signed


"type_arithmetic expressions"

1 = prefix
2 = **<u>postfixe</u>** ? 
3 = **<u>infixe</u>**
*4 = <u>**tree**</u> ?*  ?


#### Data

data is incoding with type (operation or number) in a **byte** and data according to format_number define in head

1 = operation arithmetic
2 = number
3 = parenthesis (for **<u>infixe</u>**)

##### operation define

1 = +
2 = -
3 = *
4 = /


### structure message server

serve have 2 type respons message : text or number

**Head** *metadata* ??
    "version" *version protocol*
    "type_message" *text or number*
**Data** *if depende of "type_message"*

"version" = 1 octect not signed
*version of protocol, actuelly is 1.0*

"type_message" = 1 octect not signed
1 = number
2 = text


#### Number type message

if "type_message" = number


**Data** 
    **Number**
        **Head**
            "base_number" *default is 10*
            "format_number" *default is binary*
        **Data**
            "Number"


"base_number" = 1 octect not signed
version 1 supporte *base 2*, *base 10* and *base 16*
*usually same of client base_number*

2 = base 2
10 = base 10
16 = base 16

"format_number" = 1 octect not signed
***<u>usually</u>** same of client base_number*

1 = byte_signed
2 = short_signed
3 = int_signed
4 = long_signed
5 = float_signed
6 = double_signed

7 = byte_not_signed
8 = short_not_signed
9 = int_not_signed
10 = long_not_signed
11 = float_not_signed
12 = double_not_signed



"data" = data according to format_number define in head


#### Text type message
**Data**
    **Text**
        **Head**
            "encoding_text"
            "type_message_text"
        **Data**
            "text"

##### Head

"encoding_text" = 1 octect not signed

1 = ASCII
2 = UTF-32
3 = UTF-16
4 = UTF-8


"type_message_text" = 1 octect
1 = information
2 = error

##### Data

data according to encoding_text define in head
