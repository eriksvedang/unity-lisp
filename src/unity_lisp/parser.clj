(ns unity-lisp.parser
  (:require [instaparse.core :as insta]))

(def p
  (insta/parser
    "program = <whitespace>* (form <whitespace>*)*
     <form> = token | list | vector | map | <comment>
     list = (lparen (<whitespace>* form <whitespace>*)* rparen) | emptylist
     vector = (lsquarebrack form (<whitespace> form)* rsquarebrack) | emptyvec
     map = (lcurly form (<whitespace> form)* rcurly) | emptymap
     sugar-lambda = <'#'> list
     percent-sign = '%'
     <lparen> = <'('>
     <rparen> = <')'>
     <lsquarebrack> = <'['>
     <rsquarebrack> = <']'>
     <lcurly> = <'{'>
     <rcurly> = <'}'>
     <emptylist> = lparen rparen
     <emptyvec> = lsquarebrack rsquarebrack
     <emptymap> = lcurly rcurly
     <token> = hint | word | number | infix-operator | string | accessor | method | sugar-lambda | percent-sign | keyword | keyword-fn | yield
     whitespace = #'\\s+'
     number = #'-*[0-9]+\\.?[0-9]*'
     infix-operator = (#'[\\+\\*\\/]+' | 'is' | 'as' | '-' | 'and' | '==' | '!=' | '<' | '>' | '<=' | '>=' ) <#'\\s+'>
     accessor = '.-' word
     method = '.' word
     yield = 'yield'
     hint = <'^'> word <whitespace> word
     word = #'[a-zA-Z!?]+[a-zA-Z!?.0-9-<>]*'
     string = <quote> #'[a-zA-Z!?10-9 \\-\\+\\=\\>\\<\\{\\}\\'\\[\\],.:;]*' <quote>
     quote = '\"'
     keyword = <':'> word
     keyword-fn = <'λ'> token
     comment = #';.*'"))

