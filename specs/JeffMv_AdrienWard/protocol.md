## CSAP (Client-Server Arithmetic Protocol)

Version 1.0



## TODO : 

**TODO :**

les formats des entrées des messages du client

Le format de retour du serveur

les codes d erreurs 

les commandes des opérations

Les formats des nombres (clients, serveurs)

 - clarifier tous les typesd e de ofmrat de nbrs acceptés.

Expliquer fonctionnement côté client. (). Avec exemople par ex.





## Protocol specification



### Section 1: Overview

CSAP is a text-based application-layer protocol for computing maths.

The client connects to a server and sends arithmetic operations to the server. The servers computes the expression and returns the result.  If there is an error, specific values are sent by the server to allow the client to determine what went wrong. 



### Section 2: transport layer protocol

CSAP uses TCP. The client establishes the connection. It has to know the IP address of the server. The server listens on TCP port 2277.



The server closes the connexion when:

- the client is disconnected
- the client closes the socket
- the client sends a specifc command.



### Section 3: Messages

Messages are exchanged using the encoding UTF-8.



Messages used to communicate:

- Client sends `<noVersion> <numberBase> <operation> <number1> <number2>`, each separater by a space, where 

  - `noVersion` : protocol version used

  - `numberBase` : numerical base

  - `operation` : one of the supported keywords.
    - Operands
      - ADD
      - SUB
      - MUL
      - DIV
      - EXT : (exit) close the connection
  - `number1` : left operand
  - `number2` : right operand



Server sends : 

- 



input format:

numbers have digits + `-+.`. Without the minus sign, the nnumbers are considered positiv.

Only digits



Number format (number operands, or computation result):  `[-|+] [0-9]+ .? [0-9]*` 

- optional prepending sign (`-` or `+`). Without a prepending minus sign, the numbers are considered positiv.
- digits,  Floating point notation using the dot `.`. 





Documentation of options:

- Inputs Mode : `1` decimal numbers , `2` for binary numbers, `8` for octal numbers, `16` for hexadecimal numbers, 
  - decides the format for both operands.
  
- Form
  - client sends `<noVersion> <numberBase> <operation> <number1> <number2>` .
  
    Example : `1 10 ADD -3.5  4.0` 
  
  
  
  - Server responds `<status> <result>`
  - ~~Server responds `<status>\n<result>\n<error_detail>`~~
    - PROBLEME : encodage de error detail
    - error status codes (masks)
      - `<status>`
        - `<error_detail>`
      - 0 : no error
        - 0 : error detail
      - 1 : general malformed input
        - Number of args
      - 2 : operation error
      - 4 : operand error
        - 1 : left operand error
        - 2 : right operand error
      - 8 : maths error
        - 1 : sqrt of negativ
        - 2 : division by 0
      - 16 : protocol error
        - unsupported protocol, or client message did not respect the protocol
      - 32 : other error
        - 1 : custom text error
    - `<result>` or error message
      - number if OK
      - or string if error



### Section 4: Specific Elements

Covered in this section :  Error handling



Server error codes for  `<status>`

- 0 : no error
  - 0 : error detail
- 1 : general malformed input
  - Number of args
- 2 : operation error
- 4 : operand error
  - 1 : left operand error
  - 2 : right operand error
- 8 : maths error
  - 1 : sqrt of negativ
  - 2 : division by 0
- 16 : protocol error
  - unsupported protocol, or client message did not respect the protocol
- 32 : other error
  - 1 : custom text error





### Section 5: Example Dialogs

In the following examples, the server will be at IP `10.1.2.3`



#### Example 1



The user wants the answer to the following arithmetic expression : `( -123.50 * -4.0 )` and answer should be `494`.



The client establishes the connexion to `10.1.2.3` and port `2277`.



1. Server sends the range of protocol versions it supports  `1 2`
   (Meaning the server understands protocol versions 1 to version 2).
2. Client responds [according to protocol version 1] : ``

3. The client app sends the inner part without paranthesis `()` :  
   `2 10 MUL 123.50 -4.0`    
   (which correspond to 
   `<noVersion> <numberBase> <operation> <number1> <number2>` )
4. The server responds with
   `0 494`
   (where 0 means no error, and 494 is the result of the multiplication)
5. At this point, the connexion stays open and the client can send another command / expression, or end the connexion.
6. The user wants to close the client program, so the client program sends 
    `1 0 EXT 0 0` . 
   where arguments correspond to 
   `<noVersion> <numberBase> <operation> <number1> <number2>`.
7. The server closes the socket connection.



#### Example 2

The user wants the answer to the following arithmetic expression : `( -123.50 * -4.0  + 6)` and answer should be `500`. The expression is equivalent to `( (-123.50 * -4.0)  + 6)`

Client starts with steps  1 to 4 from example 1 to get the result of the first expression `(-123.50 * -4.0)`. 

Then :

5. the client sends
   `2 10 ADD 494 6`    
6. the server responds
   `0 500`
7. [something unexpected on the client machine and] the client app is unexpectedly and unproperly closed.
8. the server keeps the socket connection open.





#### Example 3

- user wants `( (3 * -4.0 )` and answer should be `494`
- we want easy implementation of clients in other languages, so computing logic goes in server.





#### Example 4

- user wants `( (3 * -4.0) + (8 / 2) )`  :
  - the client sends each part 
- we want easy implementation of clients in other languages, so computing logic goes in server.



## Discussions / Comments



Ideas considered



**For testability and maintainability:** 

- it would be best for the protocol to be as simple as possible. It would be best to be able to test the protocol from start to finish, and in order to do that: the simpler the protocol the better, and the easier it is to test.
- The less options we offer through the protocol the less things we have to test, and the better.
  - For instance, if we return `<statusCode> <result> <errorString>`, where `<errorString>` could be deduced from the 2 other values `<statusCode>`, the field would be redundant. But even with a redundant value, we would still need to test edge cases for this redundant argument.
  -  instead of returning his training as well as the other arguments, 
    Au lieu de retourner une valeur de type string redondante en plus de nos arguments le retour de nos valeurs de retour, il est préférable de redonner le stricte minimum. C'est long en plus tester un argument redondant tel qu'une string, il faudrait pouvoir tester les quatre limites de la string, string vide, string multiligne, string dans terminé, valeur d'échappement caractère d'échappem






**Stateless vs Stateful :**

The Stateless protocol is prefered. 

And we find it better to have each server response return a complete state that the client can display. 


**Server returning a tuple of values where the error message can be deduced from other arguments**

Pros:

- Client can display off the bat an error message for most errors that happen.

Cons:

- extra string argument needs to be thoroughly tested too (with edge cases)
- language is set by the server. Language of error messages should not be set in the specification of a protocol. There could be messages in other alphabets.





----



Changement important dans la spec : 

- au lieu que ce soit le client qui dise au serveur quelle version utiliser, le serveur va annoncer à l'établissement de connexion du socket les versions du protocole supportées par le serveur (juste un range de X à Y). Exemple : `2 4` pour dire que serveur supporte versions 2 à 4.
- (le serveur envoie au client le range de version qu il supporte pour la spec. 

Dans les faits, ça veut dire qu on ne veut pas que nos utilisateurs utilisent des versions de l app client obsoilètes , ce qui forceraitt le serveur à supporter toute les 

- à l'établissement de la connexion 

