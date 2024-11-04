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



The user wants the answer to the following arithmetic expression : `( -123.50 * -4.0 )`.



The client app sends the inner part without paranthesis `()`

The client establishes the connexion to `10.1.2.3` and port `2277`.



1. Server sends the range of protocol versions it supports  `1 1`
2. Client responds [according to protocol version 1] : ``





- user wants `( -123.50 * -4.0 )` and answer should be `494`
- we want easy implementation of clients in other languages, so computing logic goes in server.

```
# client sends

```

Connexion is established





Example 2

- user wants `( (3 * -4.0 )` and answer should be `494`
- we want easy implementation of clients in other languages, so computing logic goes in server.





Example 3

- user wants `( (3 * -4.0) + (8 / 2) )`  :
  - the client sends each part 
- we want easy implementation of clients in other languages, so computing logic goes in server.



## Discussions / Comments



Ideas considered but not implemented



For maintainability: 

- it would be best for the 



----



Changement important dans la spec : 

- au lieu que ce soit le client qui dise au serveur quelle version utiliser, le serveur va annoncer à l'établissement de connexion du socket les versions du protocole supportées par le serveur (juste un range de X à Y). Exemple : `2 4` pour dire que serveur supporte versions 2 à 4.
- (le serveur envoie au client le range de version qu il supporte pour la spec. 

Dans les faits, ça veut dire qu on ne veut pas que nos utilisateurs utilisent des versions de l app client obsoilètes , ce qui forceraitt le serveur à supporter toute les 

- à l'établissement de la connexion 

