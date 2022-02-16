package com.appian.intellij.k3;

import static com.appian.intellij.k3.psi.KTypes.*;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

%%

%class KLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return NEWLINE;
%eof}

LINE_WS=[\ \t\f]
WHITE_SPACE={LINE_WS}+
NEWLINE=\r|\n|\r\n

COMMENT1="/" [^\r\n]* {NEWLINE}?
COMMENT2={WHITE_SPACE}+ {COMMENT1}

SIMPLE_COMMAND="\\"(
  [dafv_ib] // ({WHITE_SPACE}+{USER_IDENTIFIER})?
 |([ls12x]|"kr") // {WHITE_SPACE}+{USER_IDENTIFIER}
 |[egopPSTwWz] // ({WHITE_SPACE}+{NUMBER})? // 't' is not included b/c in K3 it takes an expression
 |[bBrsu\\wm]
 |[cC] //{NUMBER_VECTOR}
  )
MODE=[qk]")"
COMPLEX_COMMAND="\\"{USER_IDENTIFIER} // takes OS command and/or arbitrary expression as argument
USER_IDENTIFIER=[.a-zA-Z][._a-zA-Z0-9]*|_[a-zA-Z]+
PATH=[.a-zA-Z/][._a-zA-Z0-9/]*

K3_SYSTEM_FUNCTION=(_a|_abs|_acos|_asin|_atan|_bd|_bin|_binl|_ci|_cos|_cosh|_d|_db|_di|_div|_dj|_dot|_draw|_dv
        |_dvl|_exit|_exp|_f|_floor|_getenv|_gtime|_h|_host|_i|_ic|_in|_inv|_jd|_k|_lin|_log|_lsq|_lt|_mul|_n
        |_p|_setenv|_s|_sin|_sinh|_size|_sm|_sqr|_sqrt|_ss|_ssr|_sv|_T|_t|_tan|_tanh|_u|_vs|_v|_w)

INT_TYPE=[ihjepuvt]
FLOAT_TYPE=[fen]
Q_DATETIME_TYPE=[mdz]
TYPE={INT_TYPE}|{FLOAT_TYPE}|{Q_DATETIME_TYPE}
// only positive numbers
Q_NIL_POSITIVE="0N"({INT_TYPE}|{FLOAT_TYPE}|"g")
Q_BINARY=[01]"b"
Q_HEX_CHAR=[[:digit:]A-Fa-f]
Q_HEX_NUMBER="0x"{Q_HEX_CHAR}{Q_HEX_CHAR}?
NUMBER_POSITIVE={Q_NIL_POSITIVE}|{Q_BINARY}|{Q_HEX_NUMBER}
// positive & negative
NIL_OR_INFINITY=0[iInNwW] // iI are from k3; wW are from k4; nN are from both
Q_MONTH=[:digit:]+\.[:digit:]{2}
Q_DATE={Q_MONTH}+\.[:digit:]{2}
Q_TIME=[:digit:]+(\:[:digit:]{2}(\:[0-5][:digit:](\.[:digit:]+)?)?)?
Q_DATETIME={Q_DATE}"T"{Q_TIME}
NUMBER_POSITIVE_NEGATIVE=(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]*)?|{NIL_OR_INFINITY}
// all numbers
NUMBER_NO_TYPE=-?({NUMBER_POSITIVE_NEGATIVE})|{NUMBER_POSITIVE}|-?({Q_DATE}|{Q_TIME}|{Q_DATETIME}|{Q_MONTH})
NUMBER={NUMBER_NO_TYPE}{TYPE}?
BINARY_VECTOR=[01][01]+"b"|"0x"{Q_HEX_CHAR}{2}{Q_HEX_CHAR}+
NUMBER_VECTOR={NUMBER_NO_TYPE}({WHITE_SPACE}{NUMBER_NO_TYPE})+{TYPE}?|{BINARY_VECTOR}
C=([^\\\"]|\\[^\ \t])
CHAR=\"{C}\"
CHAR_VECTOR=\"{C}*\"
SYMBOL="`"([._a-zA-Z0-9]+|{CHAR_VECTOR})?
SYMBOL_VECTOR={SYMBOL} ({WHITE_SPACE}*{SYMBOL})+
PRIMITIVE_VERB=[!#$%&*+,-.<=>?@\^_|~]
ADVERB=("/" | \\ | ' | "/": | \\: | ':)+
CONTROL="if"|"do"|"while"
CONDITIONAL=":"|"?"|"$"|"@"|"." // ":" is from k3

%state ADVERB_STATE
%state END_OF_FILE
%state COMMAND_STATE
%state COMMENT_STATE
%state DROP_CUT_STATE

%%

<ADVERB_STATE> {
  {ADVERB}                                    { yybegin(YYINITIAL); return ADVERB;}
}

<END_OF_FILE> {
  ^"\\"{NEWLINE}                              { yybegin(YYINITIAL); return COMMENT; }
  {NEWLINE}+                                  { return COMMENT; }
  .*                                          { return COMMENT; }
}

<COMMAND_STATE> {
  // root, parent & attributes directory
  [.~\^]                                      { yybegin(YYINITIAL); return USER_IDENTIFIER; }
  {WHITE_SPACE}                               { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {USER_IDENTIFIER}                           { yybegin(YYINITIAL); return USER_IDENTIFIER; }
  {PATH}                                      { yybegin(YYINITIAL); return USER_IDENTIFIER; }
  {NUMBER}                                    { yybegin(YYINITIAL); return NUMBER; }
  {NUMBER_VECTOR}                             { yybegin(YYINITIAL); return NUMBER_VECTOR; }
  {NEWLINE}+                                  { yybegin(YYINITIAL); return NEWLINE; }
}

<COMMENT_STATE> {
  {COMMENT1}                                  { yybegin(YYINITIAL); return COMMENT;}
}

<DROP_CUT_STATE> {
  {K3_SYSTEM_FUNCTION}/{ADVERB}               { yybegin(ADVERB_STATE); return K3_SYSTEM_FUNCTION; }
  {K3_SYSTEM_FUNCTION}                        { yybegin(YYINITIAL); return K3_SYSTEM_FUNCTION; }
  "_"/{ADVERB}                                { yybegin(ADVERB_STATE); return PRIMITIVE_VERB;}
  "_"                                         { yybegin(YYINITIAL); return PRIMITIVE_VERB;}
}

<YYINITIAL> {
  {NEWLINE}+                                  { return NEWLINE; }
  ^"\\d"                                      { yybegin(COMMAND_STATE); return CURRENT_NAMESPACE; }
  ^{SIMPLE_COMMAND}                           { yybegin(COMMAND_STATE); return COMMAND; }
  ^{COMPLEX_COMMAND}                          { return COMMAND; }
  ^{MODE}                                     { return MODE; }
  {NUMBER_VECTOR}/{ADVERB}                    { yybegin(ADVERB_STATE); return NUMBER_VECTOR; }
  {NUMBER_VECTOR}/"_"                         { yybegin(DROP_CUT_STATE); return NUMBER_VECTOR; }
  {NUMBER_VECTOR}                             { return NUMBER_VECTOR; }
  [0-6]":"/[^\[]                              { return PRIMITIVE_VERB; }
  {CONTROL}/"["                               { return CONTROL; }
  {CONDITIONAL}/"["                           { return CONDITIONAL; }
  {SYMBOL_VECTOR}/{ADVERB}                    { yybegin(ADVERB_STATE); return SYMBOL_VECTOR; }
  {SYMBOL_VECTOR}                             { return SYMBOL_VECTOR; }
  {SYMBOL}/{ADVERB}                           { yybegin(ADVERB_STATE); return SYMBOL; }
  {SYMBOL}                                    { return SYMBOL; }
  {WHITE_SPACE}                               { return com.intellij.psi.TokenType.WHITE_SPACE; }
  ^{COMMENT1}                                 { return COMMENT; }
  {COMMENT2}/{NEWLINE}                        { return COMMENT; }
  {COMMENT2}                                  { return COMMENT; }

  "_"/{K3_SYSTEM_FUNCTION}                    { return PRIMITIVE_VERB;} // __sqrt 3 -> 1
  "-"/-[0-9]                                  { return PRIMITIVE_VERB;} // --6 -> 6
  {PRIMITIVE_VERB}/{ADVERB}                   { yybegin(ADVERB_STATE); return PRIMITIVE_VERB;}
  {PRIMITIVE_VERB}                            { return PRIMITIVE_VERB;}

  "("                                         { return OPEN_PAREN; }
  ")"/{ADVERB}                                { yybegin(ADVERB_STATE); return CLOSE_PAREN; }
  ")"/"_"                                     { yybegin(DROP_CUT_STATE); return CLOSE_PAREN; }
  ")"                                         { return CLOSE_PAREN; }
  ";"/{COMMENT1}                              { yybegin(COMMENT_STATE); return SEMICOLON; }
  ";"                                         { return SEMICOLON; }
  "["                                         { return OPEN_BRACKET; }
  "]"/{ADVERB}                                { yybegin(ADVERB_STATE); return CLOSE_BRACKET; }
  "]"/"_"                                     { yybegin(DROP_CUT_STATE); return CLOSE_BRACKET; }
  "]"                                         { return CLOSE_BRACKET; }
  "{"                                         { return OPEN_BRACE; }
  "}"/{ADVERB}                                { yybegin(ADVERB_STATE); return CLOSE_BRACE; }
  "}"                                         { return CLOSE_BRACE; }

  {K3_SYSTEM_FUNCTION}/{ADVERB}               { yybegin(ADVERB_STATE); return K3_SYSTEM_FUNCTION; }
  {K3_SYSTEM_FUNCTION}                        { return K3_SYSTEM_FUNCTION; }
  {USER_IDENTIFIER}/{ADVERB}                  { yybegin(ADVERB_STATE); return USER_IDENTIFIER; }
  {USER_IDENTIFIER}                           { return USER_IDENTIFIER; }
  {NUMBER}/{ADVERB}                           { yybegin(ADVERB_STATE); return NUMBER; }
  {NUMBER}/"_"                                { yybegin(DROP_CUT_STATE); return NUMBER; }
  {NUMBER}                                    { return NUMBER; }
  {CHAR}/{ADVERB}                             { yybegin(ADVERB_STATE); return CHAR; }
  {CHAR}                                      { return CHAR; }
  {CHAR_VECTOR}/{ADVERB}                      { yybegin(ADVERB_STATE); return STRING; }
  {CHAR_VECTOR}                               { return STRING; }

  ":"/{ADVERB}                                { yybegin(ADVERB_STATE); return COLON; }
  ":"                                         { return COLON; }
  "'"                                         { return SIGNAL; }
  ^"\\"{NEWLINE}                              { yybegin(END_OF_FILE); return COMMENT; }
  "\\"                                        { return TRACE; }

}

[^] { return com.intellij.psi               .TokenType.BAD_CHARACTER; }
