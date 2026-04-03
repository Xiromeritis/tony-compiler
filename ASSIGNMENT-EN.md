# The Tony programming language

### March 20, 2026

> *I call it my billion-dollar mistake. It was the invention of
> the null reference in 1965. At that time, I was designing the
> first comprehensive type system for references in an object
> oriented language (ALGOL W). My goal was to ensure
> that all use of references should be absolutely safe, with
> checking performed automatically by the compiler. But I
> couldn't resist the temptation to put in a null reference,
> simply because it was so easy to implement. This has
> led to innumerable errors, vulnerabilities, and system
> crashes, which have probably caused a billion dollars of
> pain and damage in the last forty years.*
>
> *— Tony Hoare*

<div align="center">
  <img src="figures/tony_hoare.jpg" width="268"  alt="Sir Charles Antony Richard Hoare"/>
</div>

***Figure 1:** Sir Charles Antony
Richard Hoare (1934–2026). Recipient
of the Turing Award (1980) for
his fundamental contributions
to the definition and design of
programming languages.*

---

## TOPIC

A team of two students is to design and implement
a compiler for the Tony language. The implementation language
may be either C or Java. Mutual agreement may make the choice of
another implementation language.
The use of tools is permitted and also recommended,
e.g., `flex`, `bison`, `JFlex`, `JavaCUP`, etc. More
information regarding some of these tools will be provided in
the labs.

### Deliverables, deadlines and grading

The sections of the compiler are shown in the table below.

| *Compiler module*      | *Points* | *Delivery date* |
|:-----------------------|:--------:|:----------------|
| Lexical analysis       |   1.5    | April 5, 2026   |
| Syntactic analysis     |   2.0    | April 19, 2026  |
| Semantic - Scope Check |   1.5    | May 17, 2026    |
| Semantic - Type Check  |   1.5    | May 31, 2026    |
| Code Generation        |   3.5    | July 1, 2026    |
| *Total Work*           |   10.0   |                 |

For the various sections of the project, each team must submit
the corresponding code in electronic
form by the deadline, along with clear instructions for generating an
executable program demonstrating the operation of the respective
section, from that code. The format and contents
of the deliverables, including the final
report, must comply with the instructions provided in
Section 4 of this document.

---

## Table of Contents

[**1 - Description of the Tony language**](#sec-1)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[1.1 - Tokens](#sec-1-1)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[1.2 - Data types](#sec-1-2)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[1.3 - Program structure](#sec-1-3)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.3.1 - Variables](#sec-1-3-1)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.3.2 - Structural units](#sec-1-3-2)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[1.4 - Expressions](#sec-1-4)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.4.1 - L-values](#sec-1-4-1)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.4.2 - Constants](#sec-1-4-2)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.4.3 - Operators](#sec-1-4-3)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.4.4 - Calling structural units as functions](#sec-1-4-4)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[1.5 - Statements](#sec-1-5)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[1.6 - Standard library functions](#sec-1-6)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.6.1 - Input and output](#sec-1-6-1)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.6.2 - Conversion functions](#sec-1-6-2)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1.6.3 - String handling functions](#sec-1-6-3)<br><br>
[**2 - Full Tony grammar**](#sec-2)<br><br>
[**3 - Examples**](#sec-3)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[3.1 - Say hello!](#sec-3-1)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[3.2 - The Towers of Hanoi](#sec-3-2)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[3.3 - Prime numbers](#sec-3-3)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[3.4 - String reversal](#sec-3-4)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[3.5 - Array sorting using bubble sort](#sec-3-5)<br>
&nbsp;&nbsp;&nbsp;&nbsp;[3.6 - List sorting using the partition method](#sec-3-6)<br><br>
[**4 - Submission instructions**](#sec-4)<br>

---

<a id="sec-1"></a>
## **1 – Description of the Tony language**

The Tony language is a simple imperative programming language.
Its main features, in brief, are as follows:

* A simple structure and syntax for commands and expressions that resembles
  that of some scripting languages.

* Basic data types for logical values, characters,
  integers, one-dimensional arrays, and lists.

* Simple functions, passed by value or by reference.

* Variable scope as in Pascal.

* Function library.

More details about the language are provided in the
following sections.

<a id="sec-1-1"></a>
### 1.1 - Tokens

The tokens of the Tony language are divided into the following
categories:

* *Keywords*, which are as follows:

|        |         |          |        |         |
|:-------|:--------|:---------|:-------|:--------|
| `and`  | `bool`  | `char`   | `decl` | `def`   |
| `else` | `elsif` | `end`    | `exit` | `false` |
| `for`  | `head`  | `if`     | `int`  | `list`  |
| `mod`  | `new`   | `nil`    | `nil?` | `not`   |
| `or`   | `ref`   | `return` | `skip` | `tail`  |
| `true` |         |          |        |         |

* *Identifiers*, which consist of a single letter of the Latin
  alphabet, possibly followed by a sequence of
  letters, decimal digits, underscores
  (_), or (Latin) question marks. Names must not
  coincide with the keywords mentioned
  above. Lowercase and uppercase letters are considered
  different.

* Unsigned *integer constants*, consisting
  of one or more decimal digits. Examples of integer
  constants are as follows:

|     |      |        |         |
|:----|:-----|:-------|:--------|
| `0` | `42` | `1284` | `00200` |

* *Fixed characters*, which consist of a single character
  enclosed in single quotes. This character
  can be any ordinary character or an *escape sequence*.

**Table 1:** Escape sequences.

| Escape sequence | Description                                             |
|:----------------|:--------------------------------------------------------|
| `\n`            | the line feed character                                 |
| `\t`            | the tab character                                       |
| `\r`            | the carriage return character                           |
| `\0`            | he character with ASCII code 0                          |
| `\\`            | the \ (backslash)                                       |
| `\'`            | the ' character (single quote)                          |
| `\"`            | the " character (double quote)                          |
| `\xnn`          | he character with ASCII code nn in hexadecimal notation |

Common characters are
all printable characters except for single and double
quotes and the `\` (backslash) character. Escape sequences
begin with the `\` (backslash) character
and are described in Table 1. Examples of constant
characters are as follows:

|       |       |        |        |          |
|:------|:------|:-------|:-------|:---------|
| `'a'` | `'1'` | `'\n'` | `'\''` | `'\x1d'` |

* *Literal strings*, which consist of a sequence
  of ordinary characters or escape sequences enclosed
  in double quotes. Strings cannot span
  more than one line of code.
  Examples of constant strings are as follows:

|         |              |                    |                                             |
|:--------|:-------------|:-------------------|:--------------------------------------------|
| `"abc"` | `"Route 66"` | `"Hello world!\n"` | `"Name:\t\"Douglas Adams\"\n Value:\t42\n"` |

* The *logical operators*, which are as follows:

|     |      |      |     |     |     |      |     |
|:----|:-----|:-----|:----|:----|:----|:-----|:----|
| `+` | `-`  | `*`  | `/` | `#` | `=` | `<>` | `<` |
| `>` | `<=` | `>=` |     |     |     |      |     |

* The *separators*, which are as follows:

|     |     |     |     |     |     |     |      |
|:----|:----|:----|:----|:----|:----|:----|:-----|
| `(` | `)` | `[` | `]` | `,` | `;` | `:` | `:=` |

In addition to the tokens mentioned above, a
Tony program may also contain the following, which
separate lexical units and are ignored:

* *Whitespace*, i.e., sequences consisting of
  spaces, tabs,
  line feeds, or carriage returns
  at the beginning of a line.

* *Single-line comments*, which begin with the `%` character
  and end at the end of the current line.

* *Multi-line comments*, which begin with the character sequence
  `<*` and end with the character sequence
  `*>`. Comments of this form may
  be nested.

<a id="sec-1-2"></a>
### 1.2 - Data types

Tony supports three basic data types:

* `int`: integers of at least 16 bits (`-32768`
  to `32767`),

* `char`: characters, and

* `bool`: boolean values.

In addition to the basic types, Tony also supports two
composite types:

* arrays, denoted by $t$[ ], where $t$ is a valid type,
  and

* lists, denoted by `list`[ $t$ ], where $t$ is a valid type.

Arrays and lists in Tony have referential semantics:
neither assignment nor parameter passing ever copies
the contents of an array or a list, but simply copies
the memory address where the array
or list is stored. The runtime system handles the automatic
deallocation of arrays and lists by implementing
a garbage collection algorithm.

<a id="sec-1-3"></a>
### 1.3 - Program structure

The Tony language is a block-structured language.
A program has roughly the same structure as a
Pascal. Structural units can be nested
within one another, and the scope rules are the same as those
of Pascal. The main program is a structural unit that does not
return a result and does not accept parameters.

Each structural unit may optionally contain:

* Variable declarations.

* Subprogram definitions.

* Subprogram declarations, the definitions of which will
  follow.

<a id="sec-1-3-1"></a>
#### 1.3.1 - Variables

Variable declarations begin with the keyword. They are followed by
one or more variable names, separated by commas.
Examples of declarations include:

```tony
int i
int x, y, z
char[] s
```

<a id="sec-1-3-2"></a>
#### 1.3.2 - Structural units

A structural unit is *defined* using the keyword `def`,
followed by the structural unit's header, the
local declarations, and its body. The header specifies
the return type (optional), the name of the structural unit,
and its formal parameters (optional). The return type
is omitted for structural units that do not return a result
(cf. procedures in Pascal). The formal parameters
are written within parentheses.

Each formal parameter is identified by its name,
its type, and its passing mode. The declaration of parameters
resembles that of variables. Consecutive declarations
of parameters with different types or passing modes are separated
from each other by Greek question marks (?). The
Tony language supports passing parameters by value and
by reference. If the declaration begins with the keyword
`ref`, then the declared parameters are passed by reference;
otherwise, they are passed by value.

The following are examples of header definitions for structural
units.

```tony
def p1 ()
def p2 (int n)
def p3 (int a, b; ref char c)
def int f1 (int x)
def int f2 (char[] s)
def int[][] matrix_mult(int p, q, r; int[][] a, b)
```

The local declarations of a structural unit follow the header.
Tony follows Pascal's scope rules
regarding the visibility of variable names, structural
units, and parameters.

In the case of mutually recursive subprograms,
a subprogram’s name must appear before
its definition. In this case, to avoid violating
the scope rules, a *declaration* belonging to
the header of this subprogram must precede it, without its body.
This is done using the keyword `decl` instead of `def`.

<a id="sec-1-4"></a>
#### 1.4 - Expressions

Each Tony expression has a unique type and can
be evaluated to produce a value of that type.
(The only exception is the constant `nil`, which has the type belonging to a list
of any type.) Expressions fall into two categories:
those that yield l-values, which are described in
Section 1.4.1, and those that yield r-values, which are described
in Sections 1.4.2 through 1.4.4. These two types of values are named
after their position in an assignment statement: l-values
appear on the left-hand side of the assignment, while r-values
appear on the right-hand side.

Expressions may appear within parentheses,
which are used for grouping purposes.

<a id="sec-1-4-1"></a>
#### 1.4.1 - L-values

L-values represent objects that occupy
space in the computer's memory during program execution
and that can hold values. Such objects
include variables and parameters of structural units
and array elements. Specifically:

- The name of a variable or a function parameter
  is an l-value and corresponds to that object. The type
  of the l-value is the type of the corresponding object.

- If $e_1$ is an expression of type $t$[ ] and $e_2$ is an expression
  of type `int`, then $e_1$[ $e_2$ ] is an l-value of type $t$. If the value
  of the expression $e_2$ is the non-negative integer $n$, then this
  l-value corresponds to the element with index $n$ of the array
  corresponding to $e_1$. The indexing of the array elements
  starts from zero. The value of $n$ must not exceed
  the actual bounds of the array.

If an l-value is used as an expression, its value is equal
to the value contained in the object corresponding to
it.

<a id="sec-1-4-2"></a>
#### 1.4.2 - Constants

The r-values of the Tony language include the following
constants:

- Unsigned integer constants, as described
  in Section 1.1. They have type `int` and their value is equal
  to the non-negative integer they represent.

- Character constants, as described in Section 1.1.
  They have type `char` and their value is equal to the
  character they represent.

- String literals, as described in Section 1.1.
  They have the type `char[]`. Each such expression corresponds
  to an array in which
  the characters of the string are stored. At the end of the array,
  the character `\0` is automatically stored, in accordance with the
  convention followed by the C language for strings.
  String constants are the only type belonging to constant
  of an array type that is allowed.

- The keywords `true` and `false`, which have type `bool`.

- The keyword `nil`, which represents the empty list and has
  type `list`[ $t$ ] for every valid type $t$.

<a id="sec-1-4-3"></a>
#### 1.4.3 - Operators

Tony operators are classified as operators with one or two
operands. Operators with one operand are written before it
(prefix), while operators with two operands are always written between
the operands (infix). Operands are evaluated from
left to right. Operators with two operands must evaluate
both operands, with the exception of the `and`
and `or` operators, as described below. There are also four
operators with special syntax. All Tony operators return an
r-value.

- Operators with a single operand `+` and `-` implement the
  sign operators. The operand must be an expression
  of type `int`, and the result is an r-value of the same type.

- The operator with a single operand `not` implements logical negation.
  The operand must be an expression of type `bool`, and
  the result is of the same type.

- The operators with two operands `+`, `-`, `*`, `/`, and `mod` implement
  arithmetic operations. The operands must be expressions
  of type `int`, and the result is an r-value of the same
  type.

- The operators `=`, `<>`, `<`, `>`, `<=`, and `>=` implement comparison relations
  between basic types. The operands must
  be expressions of the same basic type $t$, and the result
  is of type `bool`.

- The `and` and `or` operators implement the logical
  *AND* and *OR* operations, respectively. The operands must
  be `bool`-type expressions, and the result is also of type `bool`.
  Conditions using these
  operators are evaluated using short-circuit evaluation.

**Table 2:** Precedence and associativity of Tony's operators.

| Operators                       | Description              | Number of operands | Position and associativity |
|:--------------------------------|:-------------------------|:------------------:|:---------------------------|
| `+`, `-`                        | Signs                    |         1          | prefix                     |
| `*`, `/`, `mod`                 | Multiplication operators |         2          | infix, left                |
| `+`, `-`                        | Addition operators       |         2          | infix, left                |
| `#`                             | List construction        |         2          | infix, right               |
| `=`, `<>`, `<`, `>`, `<=`, `>=` | Relational operators     |         2          | infix, none                |
| `not`                           | Logical negation         |         1          | prefix                     |
| `and`                           | Logical conjunction      |         2          | infix, left                |
| `or`                            | Logical disjunction      |         2          | infix, left                |

That is, if the result of the condition is known from
evaluating only the first operand, the second
operand is not evaluated at all.

- The `#` operator implements the construction of a non-empty list (cons).
  The first operand must be an expression of some valid
  type $t$ — the head — and the second operand must
  be an expression of type `list`[ $t$ ] — the tail. The result
  is also of type `list`[ $t$ ].

- The unary operators `nil?`, `head`, and `tail` implement
  three basic list operations. Their syntax resembles
  function calls. The operand is written inside parentheses
  and must be of type `list`[ $t$ ], for some
  valid type $t$. The `nil?` operator returns a result of type
  `bool`: it checks whether a list is empty or not. The
  `head` operator returns a result of type $t$: the head of the list.
  The `tail` operator returns a result of type `list`[ $t$ ]: the
  tail of the list. The `head` and `tail` operators must not
  be called on lists that are empty.

- The `new` operator is used to create arrays
  and has the special syntax `new` $t$[ $e$ ]. $t$ must be
  a valid type, and the expression $e$ must be of type
  `int`. The value of $e$ must be a positive integer
  $n$. The result has type $t$[ ] and is a new array
  of $n$ elements, whose initial values are unspecified.

Table 2 defines the precedence and associativity
of Tony’s operators. The lines
higher in the table contain operators of higher precedence.
Operators on the same row have
the same precedence. The operators `nil?`, `head`, `tail`, and `new`, which
have special syntax, are not included in the table.

<a id="sec-1-4-4"></a>
#### 1.4.4 – Calling structural units as functions

If $f$ is the name of a structural unit with a valid return type
$t$, then the expression $f$($e_1$, … , $e_n$) is an r-value of type
$t$. The number of actual parameters $n$ must match
with the number of formal parameters of $f$. Furthermore, the type
and pass-by-value nature of each actual parameter must match
the type and pass-by-value nature of the corresponding formal
parameter, according to the following rules.

- If the formal parameter is of type $t$ and is passed by value,
  then the corresponding actual parameter must be
  an expression of type $t$.

- If the formal parameter is of type $t$ and is passed by reference,
  then the corresponding actual parameter must
  be an l-value of type $t$.

When calling a structural unit, the actual parameters
are evaluated from left to right.

<a id="sec-1-5"></a>
#### 1.5 - Statements

The statements supported by the Tony language are divided into
simple and compound statements. The simple statements are as follows:

- The empty statement `skip`, which does nothing.

- The assignment statement $l$ := $e$, which assigns the value of the expression
  $e$ to the l-value $l$. The l-value $l$ and the expression $e$ must
  be of the same type $t$. The l-value $l$ must not correspond
  to an element of a constant string. As
  described in Section 1.2, the assignment of arrays and
  lists copies the address of an array or a list,
  creating aliases—it never copies the contents.

- The function call statement. The actual parameters
  are written between parentheses. If there are multiple parameters,
  they are separated by commas. The conditions for the
  call are the same as in Section 1.4.4, with the difference
  that no return type must be specified for the
  structural unit.

Tony’s compound commands are as follows:

- The `if` control statement $e_1$ : $b_1$ `elsif` $e_2$ : $b_2$ `else` : $b_2$, where the
  `elsif` branch may be repeated zero or more
  times and the `else` branch is optional. $e_1$
  and $e_2$ must be valid `bool` expressions. $b_1$,
  $b_2$, and $b_3$ must be sequences of one or more
  statements. The semantics of the statement are the same as, for example, in
  Python.

- The `for` control statement $s_1$; $e$; $s_2$ : $b$, which resembles the corresponding
  statement in C. $s_1$ and $s_2$ are sequences of one or
  more simple statements, separated by
  commas. $s_1$ is executed once at the start of the loop
  (initialization), while $s_2$ is executed at the end of each iteration
  (step). The expression $e$ must be of type `bool` and
  represents the loop condition: as long as its value is
  true, the loop repeats. $b$ must be
  a sequence of one or more statements.

- The `exit` jump instruction, which terminates the execution of the
  current structural unit. This instruction must appear
  in the body of a structural unit with no return type.

- The return $e$ jump instruction, which terminates the execution of the
  current block and returns
  the value of the expression $e$ as the result. This statement must appear
  in the body of a block with return type $t$
  and the expression $e$ must have the same type $t$.

<a id="sec-1-6"></a>
#### 1.6 - Standard library functions

Tony supports a set of predefined building
blocks, which you will need to implement in the optional
part of the assignment. They are visible in every structural unit, unless
they are shadowed by variables, parameters, or other structural
units with the same name. Their declarations are given below,
and their operation is explained.

<a id="sec-1-6-1"></a>
#### 1.6.1 - Input and output

```tony
decl puti (int n)
decl putb (bool b)
decl putc (char c)
decl puts (char[] s)
```

These functions are used to print values
that belong to Tony's basic types, as well as to
print strings.

```tony
decl int geti ()
decl bool getb ()
decl char getc ()
decl gets (int n, char[] s)
```

Similarly, the above functions are used to
input values belonging to Tony's basic types and
to input strings. The `gets` function is used
to read a string up to the next
newline character. Its parameters specify the
maximum number of characters (including the terminating
`'\0'`) that may be read and the character array
into which they will be placed. The newline character
is not stored. If the array size is exhausted
before a newline character is encountered, reading will
resume later from the point where it was interrupted.

<a id="sec-1-6-2"></a>
#### 1.6.2 – Conversion functions

```tony
decl int abs (int n)
decl int ord (char c)
decl char chr (int n)
```

The `abs` function calculates the absolute value of an integer.
The `ord` and `chr` functions convert a character
to its corresponding ASCII code and vice versa.

<a id="sec-1-6-3"></a>
#### 1.6.3 - String handling functions

```tony
decl int strlen (char[] s)
decl int strcmp (char[] s1, s2)
decl strcpy (char[] trg, src)
decl strcat (char[] trg, src)
```

These functions work exactly the same way as
their counterparts in the C language's standard library.

---

<a id="sec-2"></a>
## 2 - Full Tony grammar

The syntax of the Tony language is given below in
EBNF form. The grammar that follows is *ambiguous*, but most
ambiguities can be resolved by taking into account
the rules of precedence and associativity
of operators, as described in Table 2. The symbols
⟨ $id$ ⟩, ⟨ $int-const$ ⟩, ⟨ $char-const$ ⟩, and ⟨ $string-literal$ ⟩ are terminal
symbols of the grammar.

|                   |        |                                                                                                                  |
|:------------------|:------:|:-----------------------------------------------------------------------------------------------------------------|
| ⟨ $program$ ⟩     |  ::=   | ⟨ $func-def$ ⟩                                                                                                   |
| ⟨ $func-def$ ⟩    |  ::=   | “`def`” ⟨ $header$ ⟩ “:” (⟨ $func-def$ ⟩ &#124; ⟨ $func-decl$ ⟩ &#124; ⟨ $var-def$ ⟩)* ⟨ $stmt$ ⟩+ “`end`”       |
| ⟨ $header$ ⟩      |  ::=   | [ ⟨ $type$ ⟩ ] ⟨ $id$ ⟩ “`(`” [ ⟨ $formal$ ⟩ (“;” ⟨ $formal$ ⟩)* ] “`)`”                                         |
| ⟨ $formal$ ⟩      |  ::=   | [ “ $ref$ ” ] ⟨ $type$ ⟩ ⟨ $id$ ⟩ (“,” ⟨ $id$ ⟩)*                                                                |
| ⟨ $type$ ⟩        |  ::=   | “`int`” &#124; “`bool`” &#124; “`char`” &#124; ⟨ $type$ ⟩ “`[`” “`]`” &#124; “`list`” “[” ⟨ $type$ ⟩ “]”         |
| ⟨ $func-decl$ ⟩   |  ::=   | “`decl`” ⟨ $header$ ⟩                                                                                            |
| ⟨ $var-def$ ⟩     |  ::=   | ⟨ $type$ ⟩ ⟨ $id$ ⟩ (“,” ⟨ $id$ ⟩)*                                                                              |
| ⟨ $stmt$ ⟩        |  ::=   | ⟨ $simple$ ⟩ &#124; “`exit`” &#124; “`return`” ⟨ $expr$ ⟩                                                        |
|                   | &#124; | “`if`” ⟨ $expr$ ⟩ “:” ⟨ $stmt$ ⟩+ (“`elif`” ⟨ $expr$ ⟩ “:” ⟨ $stmt$ ⟩+)* [ “`else`” “:” ⟨ $stmt$ ⟩+ ] “`end`”    |
|                   | &#124; | “`for`” ⟨ $simple-list$ ⟩ “;” ⟨ $expr$ ⟩ “;” ⟨ $simple-list$ ⟩ “:” ⟨ $stmt$ ⟩+ “`end`”                           |
| ⟨ $simple$ ⟩      |  ::=   | “`skip`” &#124; ⟨ $atom$ ⟩ “:=” ⟨ $expr$ ⟩ &#124; ⟨ $call$ ⟩                                                     |
| ⟨ $simple-list$ ⟩ |  ::=   | ⟨ $simple$ ⟩ (“,” ⟨ $simple$ ⟩)*                                                                                 |
| ⟨ $call$ ⟩        |  ::=   | ⟨ $id$ ⟩ “(” [ ⟨ $expr$ ⟩ (“,” ⟨ $expr$ ⟩)* ] “)”                                                                |
| ⟨ $atom$ ⟩        |  ::=   | ⟨ $id$ ⟩ &#124; ⟨ $string-literal$ ⟩ &#124; ⟨ $atom$ ⟩ “[” ⟨ $expr$ ⟩ “]” &#124; ⟨ $call$ ⟩                      |
| ⟨ $expr$ ⟩        |  ::=   | ⟨ $atom$ ⟩ &#124; ⟨ $int-const$ ⟩ &#124; ⟨ $char-const$ ⟩ &#124; “(” ⟨ $expr$ ⟩ “)”                              |
|                   | &#124; | ( “+” &#124; “-” ) ⟨ $expr$ ⟩ &#124; ⟨ $expr$ ⟩ (“+” &#124; “-” &#124; “*” &#124; “/” &#124; “`mod`”) ⟨ $expr$ ⟩ |
|                   | &#124; | ⟨ $expr$ ⟩ (“=” &#124; “<>” &#124; “<” &#124; “>” &#124; “<=” &#124; “>=”) ⟨ $expr$ ⟩                            |
|                   | &#124; | “`true`” &#124; “`false`” &#124; “`not`” ⟨ $expr$ ⟩ &#124; ⟨ $expr$ ⟩ (“`and`” &#124; “`or`”) ⟨ $expr$ ⟩         |
|                   | &#124; | “`new`” ⟨ $type$ ⟩ “[” ⟨ $expr$ ⟩ “]” &#124; “`nil`” &#124; “`nil?`” “(” ⟨ $expr$ ⟩ “)”                          |
|                   | &#124; | ⟨ $expr$ ⟩ “#” ⟨ $expr$ ⟩ &#124; “`head`” “(” ⟨ $expr$ ⟩ “)” &#124; “`tail`” “(” ⟨ $expr$ ⟩ “)”                  |

---

<a id="sec-3"></a>
## 3 - Examples

This section provides six examples of programs
written in the Tony language, the complexity of which varies
considerably.

<a id="sec-3-1"></a>
### 3.1 – Say hello world!

The following example is the simplest program in the
Tony language that produces an output visible to the user.
This program simply prints a message.

```tony
def hello ():
  puts("Hello world!\n")
end
```

<a id="sec-3-2"></a>
### 3.2 - The Towers of Hanoi

The following program solves the Tower of Hanoi
problem. A brief description of the problem is provided
below.

<div align="center">
  <img src="figures/hanoi.jpg" width="1258"  alt="Towers of Hanoi"/>
</div>

**Figure 2:** The Towers of Hanoi.

There are three pegs, the first of which has
𝑛 rings on it. The outer diameters of the
rings are different, and they are strung
from bottom to top in descending order of outer diameter,
as shown in Figure 2. The task is to move the rings
from the first to the third pole (using the second
as a staging area), but following these rules:

- Only one ring may be moved at a time,
  from one pole to another.

- It is forbidden to place a ring with a larger
  diameter on top of a ring with a smaller diameter.

The program in the Tony language that solves this problem
is given below. The function `hanoi` is recursive.

```tony
def solve ():

  def hanoi (int rings; char[] source, target, auxiliary):

    def move (char[] source, target):
      puts("Moving from ") puts(source) puts(" to ") puts(target) puts(".\n")
    end

    if rings >= 1:
      hanoi(rings-1, source, auxiliary, target)
      move(source, target)
      hanoi(rings-1, auxiliary, target, source)
    end
  end

  int NumberOfRings

  puts("Rings: ")
  NumberOfRings := geti()
  hanoi(NumberOfRings, "left", "right", "middle")
end
```

<a id="sec-3-3"></a>
### 3.3 – Prime numbers

The following program example in the Tony language
is a program that calculates the prime numbers between
1 and $n$, where $n$ is specified by the user. This program
uses a simple algorithm to calculate the
prime numbers. A pseudocode formulation of this algorithm
is given below. It is assumed that the numbers 2
and 3 are prime, and then only numbers
of the form 6 $k$ ± 1 are considered, where $k$ is a natural number.

**Main Program**

Print the numbers 2 and 3<br>
For $t$ := 6 up to $n$ with a step size of 6, do the following:<br>
&nbsp;&nbsp;&nbsp;&nbsp;If the number $t$ - 1 is prime, then print it<br>
&nbsp;&nbsp;&nbsp;&nbsp;if the number $t$ + 1 is prime, then print it

**Checking algorithm (is the number $t$ prime?)**

if $t$ < 0, then check the number - $t$<br>
if $t$ < 2, then $t$ is not prime<br>
if $t$ = 2, then $t$ is prime<br>
if $t$ is divisible by 2, then $t$ is not prime<br>
for $i$ ∶= 3 up to $t$/2 with step 2, do the following:<br>
&nbsp;&nbsp;&nbsp;&nbsp;if $t$ is divisible by $i$, then $t$ is not prime<br>
$t$ is prime

The corresponding program in the Tony language is as follows.

```tony
def primes ():

  def bool prime (int n):
    int i
    if n < 0: return prime(-n)
    elsif n < 2: return false
    elsif n = 2: return true
    elsif n mod 2 = 0: return false
    else:
      for i := 3; i <= n / 2; i := i+2:
        if n mod i = 0:
          return false
        end
      end
      return true
    end
  end

  int limit, number, counter

  limit := 181 % there are 42 prime numbers lower than 181
  counter := 0
  if limit >= 2: counter := counter + 1 end
  if limit >= 3: counter := counter + 1 end
  for number := 6; number <= limit; number := number + 6:
    if prime(number-1):
      counter := counter + 1
    end
    if (number <> limit) and prime(number+1):
      counter := counter + 1
    end
  end
  puti(counter)
end
```

<a id="sec-3-4"></a>
### 3.4 - String reversal

The following program in the Tony language prints the
message `“Hello world!”` by reversing the given string.

```tony
def main ():

  def char[] reverse (char[] s):
    char[] t
    int i, l

    l := strlen(s)
    t := new char[l+1]
    for i := 0; i < l; i := i+1: t[i] := s[l-i-1] end
    t[i] := '\0'
    return t
  end

  puts(reverse("\n!dlrow olleH"))
end
```

<a id="sec-3-5"></a>
### 3.5 - Array sorting using bubble sort

The bubble sort algorithm is one of the
best-known and simplest sorting algorithms. The following
program in Tony uses it to sort
an array of integers in ascending order. If $x$ is
the array to be sorted and 𝑛 is its size
(we assume, according to Tony’s convention, that its elements
are $x$[0], $x$[1], … $x$[ $n$ − 1 ]), a variation of the algorithm
is described in pseudocode as follows:

**Bubble sort**

repeat the following:<br>
&nbsp;&nbsp;&nbsp;&nbsp;for $i$ from 0 to $n$ − 2<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if $x$[𝑖] > $x$[ $i$ + 1 ]<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;swap $x$[ $i$ ] and $x$[ $i$ + 1 ]<br>
as long as the order of the elements of $x$ changes

The corresponding program in Tony is as follows:

```tony
def main ():

  def bsort (int n; int[] x):

    def swap (ref int x, y):
      int t
      t := x
      x := y
      y := t
    end

    int i

    bool changed

    for changed := true; changed; skip:
      changed := false
      for i := 0; i < n-1; i := i+1:
        if x[i] > x[i+1]:
          swap(x[i], x[i+1])
          changed := true
        end
      end
    end
  end

  def writeArray (char[] msg; int n; int[] x):
    int i

    puts(msg)
    for i := 0; i < n; i := i+1:
      if i > 0: puts(", ") end
        puti(x[i])
      end
    puts("\n")
    end

  int seed, i
  int[] x

  x := new int[16]
  seed := 65
  for i := 0; i < 16; i := i+1:
    seed := (seed * 137 + 220 + i) mod 101
    x[i] := seed
  end
  writeArray("Initial array: ", 16, x)
  bsort(16, x)
  writeArray("Sorted array: ", 16, x)
end
```

<a id="sec-3-6"></a>
### 3.6 - List sorting using the partition method

The quicksort algorithm could not, of course,
be missing from the Tony language specification manual! Its
(almost) functional variant for sorting lists,
described below, differs from the
imperative original form attributed to Tony Hoare
which sorts an array in place.

**Quicksort Algorithm**

use the first element of the list as the pivot
(pivot)<br>
divide the remaining elements of the list into two lists:<br>
&nbsp;&nbsp;&nbsp;&nbsp;one containing the elements that are smaller than
pivot, and<br>
&nbsp;&nbsp;&nbsp;&nbsp;one containing the elements that are greater than or equal to<br>
sort the two lists separately (recursively)<br>
merge the results, inserting the pivot

Instead of separate sorting and merging, the function
`qsort_aux(l, r)` is used, which sorts the list l and
simultaneously merges the list r into the result. The program
in the Tony language is as follows:

```tony
def main ():
  def list[int] qsort (list[int] l):
    def list[int] qsort_aux (list[int] l, rest):
      int pivot, x
      list[int] lt, ge

      if nil?(l): return rest end
      pivot := head(l)
      l := tail(l)
      for lt := nil, ge := nil; nil?(l); l := tail(l):
        x := head(l)
        if x < pivot: lt := x # lt else: ge := x # ge end
      end
      return qsort_aux(lt, pivot # qsort_aux(ge, rest))
    end
    return qsort_aux(l, nil)
  end

  def writeList (char[] msg; list[int] l):
    bool more

    puts(msg)
    for more := false; not nil?(l); l := tail(l), more := true:
      if more: puts(", ") end
      puti(head(l))
    end
    puts("\n")
  end

  int seed, i
  list[int] l

  seed := 65
  for i := 0, l := nil; i < 16; i := i+1:
    seed := (seed * 137 + 220 + i) mod 101
    l := seed # l
  end
  writeList("Initial list: ", l)
  l := qsort(l)
  writeList("Sorted list: ", l)
end
```

---

<a id="sec-4"></a>
## 4 - Submission instructions

The compiler will accept the source program from a
file with any extension (e.g., `*.tony`) that is provided as
its sole argument. The output of the semantic analysis
will be the abstract syntax tree corresponding to the
program along with the corresponding information for each node
(e.g., variable types).

If you do not implement the entire compiler,
you must provide clear instructions and an executable program
that can demonstrate the operation of each
section.

The value returned to the operating system by
the compiler should be zero in the case of
successful compilation and non-zero otherwise.
