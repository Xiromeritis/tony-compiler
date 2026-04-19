package gr.hua.dit.compilers;

import java_cup.runtime.*;

%%

%class Lexer
%unicode
%cup
%line
%column
%public

/* State for nested multi-line comments */
%state COMMENT

%{
  /* Return tokens to the parser */
  private Symbol symbol(int type) {
    return new Symbol(type, yyline + 1, yycolumn + 1);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline + 1, yycolumn + 1, value);
  }

  /* Counter for nested comment depth */
  private int comment_depth = 0;

  /* Convert JFlex string into Java Character object */
  private Character parseChar(String text) {
      // Remove the surrounding single quotes
      String inner = text.substring(1, text.length() - 1);

      // Simple character
      if (inner.length() == 1) {
          return inner.charAt(0);
      }
      // Hexadecimal escape sequence
      else if (inner.startsWith("\\x")) {
          int hexValue = Integer.parseInt(inner.substring(2), 16);
          return (char) hexValue;
      }
      // Standard escape sequences
      else {
          switch (inner) {
              case "\\n": return '\n';
              case "\\t": return '\t';
              case "\\r": return '\r';
              case "\\0": return '\0';
              case "\\\\": return '\\';
              case "\\'": return '\'';
              case "\\\"": return '\"';
              default: return inner.charAt(0); // Fallback
          }
      }
  }
%}

/* Catch unclosed multi-line comments at the EOF */
%eofval{
  if (yystate() == COMMENT) {
    throw new RuntimeException("Lexical Error: Reached End Of File (EOF) but the multi-line comment <* was never closed!");
  }
  return symbol(sym.EOF);
%eofval}

/* Macros */

Alpha  = [A-Za-z]
Digit  = [0-9]
XDigit = [A-Fa-f0-9]
Space  = [ \t\n\r]+

Identifier  = {Alpha} ({Alpha} | {Digit} | "_" | "?")*
Integer     = {Digit}+
LineComment = "%" [^\r\n]*
Escape      = \\[ntr0\'\"\\] | \\x{XDigit}{2}
CommonChar  = [^\"\'\\]

Character   = \'({CommonChar} | {Escape})\'
String      = \"({CommonChar} | {Escape})*\"

%%

/* Rules - Normal state (<YYINITIAL>) */

<YYINITIAL> {

  /* Keywords */
  "and"     { return symbol(sym.AND); }
  "bool"    { return symbol(sym.BOOL); }
  "char"    { return symbol(sym.CHAR); }
  "decl"    { return symbol(sym.DECL); }
  "def"     { return symbol(sym.DEF); }
  "else"    { return symbol(sym.ELSE); }
  "elsif"   { return symbol(sym.ELSIF); }
  "end"     { return symbol(sym.END); }
  "exit"    { return symbol(sym.EXIT); }
  "false"   { return symbol(sym.FALSE); }
  "for"     { return symbol(sym.FOR); }
  "head"    { return symbol(sym.HEAD); }
  "if"      { return symbol(sym.IF); }
  "int"     { return symbol(sym.INT); }
  "list"    { return symbol(sym.LIST); }
  "mod"     { return symbol(sym.MOD); }
  "new"     { return symbol(sym.NEW); }
  "nil"     { return symbol(sym.NIL); }
  "nil?"    { return symbol(sym.NILQ); }
  "not"     { return symbol(sym.NOT); }
  "or"      { return symbol(sym.OR); }
  "ref"     { return symbol(sym.REF); }
  "return"  { return symbol(sym.RETURN); }
  "skip"    { return symbol(sym.SKIP); }
  "tail"    { return symbol(sym.TAIL); }
  "true"    { return symbol(sym.TRUE); }

  /* Operators */
  "+"       { return symbol(sym.PLUS); }
  "-"       { return symbol(sym.MINUS); }
  "*"       { return symbol(sym.MULT); }
  "/"       { return symbol(sym.DIV); }
  "#"       { return symbol(sym.HASH); }
  "="       { return symbol(sym.EQ); }
  "<>"      { return symbol(sym.NEQ); }
  "<"       { return symbol(sym.LT); }
  ">"       { return symbol(sym.GT); }
  "<="      { return symbol(sym.LEQ); }
  ">="      { return symbol(sym.GEQ); }

  /* Separators */
  "("       { return symbol(sym.LPAR); }
  ")"       { return symbol(sym.RPAR); }
  "["       { return symbol(sym.LBRAC); }
  "]"       { return symbol(sym.RBRAC); }
  ","       { return symbol(sym.COMMA); }
  ";"       { return symbol(sym.SEMICOLON); }
  ":"       { return symbol(sym.COLON); }
  ":="      { return symbol(sym.ASSIGN); }

  /* Constants and identifiers */
  {Integer} {
      // Warning for leading zeros (interpreted as decimal anyway)
      if (yytext().length() > 1 && yytext().startsWith("0")) {
          System.err.println("Warning (Line " + (yyline + 1) + "): The number " + yytext() + " starts with 0. It will be parsed as a decimal.");
      }

      try {
          int value = Integer.parseInt(yytext());
          // Check bounds (16-bit max = 32767)
          if (value > 32767 + 1) {
              throw new RuntimeException("Lexical Error: Integer " + value + " is out of bounds (max 32767) at line " + (yyline + 1));
          }
          return symbol(sym.INTEGER, value);
      } catch (NumberFormatException e) {
          // Catches astronomically large numbers that don't fit in an int
          throw new RuntimeException("Lexical Error: Number " + yytext() + " is astronomically large at line " + (yyline + 1));
      }
  }

  /* Use parseChar() to return Java Character */
  {Character}    { return symbol(sym.CHARACTER, parseChar(yytext())); }

  {String}       { return symbol(sym.STRING, yytext()); }

  {Identifier} {
      // Check identifier length (max 64 characters)
      if (yytext().length() > 64) {
          throw new RuntimeException("Lexical Error: Identifier '" + yytext().substring(0,15) + "...' exceeds 64 characters at line " + (yyline + 1));
      }
      return symbol(sym.ID, yytext());
  }

  /* Spaces and single-line comments */
  {Space}        { /* Ignore whitespace */ }
  {LineComment}  { /* Ignore single-line comments */ }

  /* Multi-line comments start */
  "<*" {
    yybegin(COMMENT);
    comment_depth = 1;
  }
}

/* Rules - Comment state (<COMMENT>) */

<COMMENT> {
  /* Found a nested comment */
  "<*" {
    comment_depth++;
  }

  /* Closing a comment */
  "*>" {
    comment_depth--;
    if (comment_depth == 0) {
      yybegin(YYINITIAL); /* Back to normal state */
    }
  }

  /* Ignore everything else inside comments */
  [^] { /* Do nothing */ }
}

/* Error fallback */
[^] { throw new RuntimeException("Illegal character <"+yytext()+"> at line " + (yyline + 1) + ", column " + (yycolumn + 1)); }
