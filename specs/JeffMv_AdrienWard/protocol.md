## CSAP (Client-Server Arithmetic Protocol)

Version 2.0



The goal of this protocol is to allow computation of mathematical expressions in a client-server architecture.

The protocol is designed in a way that enables it to evolve and be able to handle more complex mathematical operations. In order to do that we have prepared a set of communication formats  zo handle those future changes.





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

  - `numberBase` : numerical base the number is written in. 
    This version of the protocol only supports base 10. `numberBase`  takes value 10 to indicate that the numbers used as operands will the response will be written in base 10.
  
  - `operation` : one of the supported keywords.
    - Operands
      - `ADD` : addition
      - `SUB` : subtraction
      - `MUL` : multiplication
      - `DIV` : division
      - `EXT` : (exit) the server will close the connection
  - `number1` : left operand (number)
  - `number2` : right operand (number)



- Server responses follow the format  `<status> <result>`, where both are numerical values.
  - status is an integer
  - result can be a floating point number or an integer, depending on the context.



Number format:

numbers have digits and `-+.`. Without the minus sign, the numbers are considered positiv. A decimal number uses `.` as the floating point separator. Scientific notation is not supported.



Documentation of options:

- Inputs Mode : `1` decimal numbers , `2` for binary numbers, `8` for octal numbers, `16` for hexadecimal numbers, 
  - decides the format for both operands.
  
- Form
  - client sends `<noVersion> <numberBase> <operation> <number1> <number2>` .
  
    Example : `1 10 ADD -3.5  4.0` 
  
  
  

### Section 4: Specific Elements

Covered in this section :  Error handling



Server responds `<status> <result>`
In case of an error (`status != 0`), and the `result` takes a mask of values to indicate all the error details, when applicable.



Bitmask values for `status` :

- `0` : no error

  - `result` contains the result of the computation

- `1` : general malformed input

  - `result` has value 0

- `2` : operation error

  - `result` has value 0

- `4` : operand error

  `result` takes value as a mask:

  - `1` : left operand error
  - `2` : right operand error

- `8` : maths error
  `result` takes value as a mask:

  - `1` : sqrt of negativ
  - `2` : division by 0

- `16` : protocol error. 

  `result` takes value as a mask:

  - `0`: unsupported protocol
  - `1`: client message did not respect the protocol

- `32` : other error

  - `result` has value 0





### Section 5: Example Dialogs

In the following examples, the server will be at IP `10.1.2.3`



#### Example 1



The user wants the answer to the following arithmetic expression : `( -123.50 * -4.0 )` and answer should be `494`.



The client establishes the connexion to `10.1.2.3` and port `2277`.



1. Server sends informations about its supported operations with the following message format:
   `WELCOME:PROTOCOL:<min_protocol>:<max_protocol>:<operation_name> [<operation_name> ...]`

   Example of real message : `WELCOME:PROTOCOL:1:2:ADD,SUB,MUL,DIV,EXT`
   (Meaning the server understands protocol versions 1 to version 2, and the 5 operations commands `ADD,SUB,MUL,DIV,EXT`).

2. The server sends an integer `N` followed by a new line, to indicate that the next `N` lines are a documentation of the expected exchange format.
   Example:

   ```
   3
   Opérations supportées: <operation> <operand1> <operand2>
   Avec <operation> prenant une valeur parmi : ADD,SUB,MUL,DIV,EXT
   Valeurs acceptées pour <operandX>: nombre à virgule
   ```

   

3. [The client app can display the message to the end user].

4. The client app sends the inner part of the desired expression, without paranthesis :  
   `2 10 MUL 123.50 -4.0`    
   (which correspond to 
   `<noVersion> <numberBase> <operation> <number1> <number2>` )

5. The server responds with
   `0 494`
   (where 0 means no error, and 494 is the result of the multiplication)

6. At this point, the connexion stays open and the client can send another command / expression, or end the connexion.

7. The user wants to close the client program, so the client program sends 
   `1 0 EXT 0 0` . 
   where arguments correspond to 
   `<noVersion> <numberBase> <operation> <number1> <number2>`.

8. The server closes the socket connection.



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

The opposite would be to have multiple exchanges of 1 value each

A stateful protocol would be a problem

 Un protocole avec État aurait été un problème, par exemple si il y a un Ping ou une interférence réseau, l'interfaces utilisateurs et l'expérience utilisateur se trouverait dégradé.

Chaque fois qu'il y a un échange sur le réseau, il y a une possibilité de cette échange et de la latence ou bien Quilier une erreur de réseau et que le echange soit coupé.

De plus, si l'on prend le cas d'une connexion avec un serveur sur un continent américain, et le client dans le continent européen, chaque échange de messages serait ralenti par le Ping par la latence entre les deux continents, et cela se ressentirais sur l'expérience utilisateur.

Il est donc préférable, dans la mesure du possible, de utiliser un protocole sans états, pour éviter des interruption intermittente en plein milieu d'un échange critique dans le protocole. are returning a taper off values where is the error message in dirty stuff returned values



**Server returning a tuple of values where the error message can be deduced from other arguments**

Pros:

- Client can display off the bat an error message for most errors that happen.

Cons:

- extra string argument needs to be thoroughly tested too (with edge cases)
- language is set by the server. Language of error messages should not be set in the specification of a protocol. There could be messages in other alphabets.

Au lieu de retourner avec nos codes d'erreur et notre valeur calculée, au lieu de retourner aussi un message d'erreur de type string que le serveur donne au client, on aurait simplement afficher des messages d'erreur. On pourrait simplement donner la signification des messages d'erreur. L'interprétation des messages d'erreur se fera dans une documentation séparée, et les cadeaux ont été documents dans le protocole.

L'un des désavantages de retourner une string de message d'erreur et que la langue est définie par le serveur. Le message doit ensuite être localisé par le client, sinon les messages s'affiche en français en anglais pardon sur le client. Autant dans ce cas utiliser directement les codes d'erreur, car nous souhaitons.

La spécifications d'un protocole ne devrait pas dire la langue dans laquelle est faite la communication. Le message Dereure envoyé par le serveur pourrait être en français ou être en anglais ou en allemand d'autres langues, et pour éviter de devoir mentionner pour chaque serveur Callelongue le serveur parle, nous choisissons de ne garder que les codes d'erreur et non pas les messages d'erreur dans la spécifications du protocole.





----



Changement important dans la spec : 

- au lieu que ce soit le client qui dise au serveur quelle version utiliser, le serveur va annoncer à l'établissement de connexion du socket les versions du protocole supportées par le serveur (juste un range de X à Y). Exemple : `2 4` pour dire que serveur supporte versions 2 à 4.
- (le serveur envoie au client le range de version qu il supporte pour la spec. 

Dans les faits, ça veut dire qu on ne veut pas que nos utilisateurs utilisent des versions de l app client obsoilètes , ce qui forceraitt le serveur à supporter toute les 

- à l'établissement de la connexion 

