



input format:

numbers have digits + `-+.`. Without the minus sign, the nnumbers are considered positiv.

Only digits



Documentation of options:

- Inputs Mode : `1` for scientific numbers , `2` for binary numbers, `3` for octal numbers, `4` for hexadecimal numbers
  - decides the format for both operands.
- Form
  - client sends `1 ADD -3.5  4.0`
  - Server responds `<status> <error_detail> <result>`
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
      - 16 : other error
        - 1 : custom text error
    - `<result>` or error message
      - number if OK
      - or string if error



Scenario:

Example 1

- user wants `( -123.50 * -4.0 )` and answer should be `494`
- we want easy implementation of clients in other languages, so computing logic goes in server.



Example 2

- user wants `( (3 * -4.0 )` and answer should be `494`
- we want easy implementation of clients in other languages, so computing logic goes in server.





Example 3

- user wants `( (3 * -4.0) + (8 / 2) )`  :
  - the client sends each part 
- we want easy implementation of clients in other languages, so computing logic goes in server.



