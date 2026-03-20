package gr.hua.dit.compilers;

import java_cup.runtime.*;

%%

%class Lexer
%unicode
%cup
%line
%column
%public

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

%%

/* error fallback */
[^] { throw new Error("Illegal character <"+yytext()+">"); }
