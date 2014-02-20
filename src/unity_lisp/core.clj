(ns unity-lisp.core
  (:require [instaparse.core :as insta]
            [clojure-watch.core :refer [start-watch]]
            [clojure.core.match :refer [match]]))

(def p
  (insta/parser
    "program = (form <whitespace>*)*
     <form> = token | list | vector | map
     list = (lparen form (<whitespace> form)* rparen) | emptylist
     vector = (lsquarebrack form (<whitespace> form)* rsquarebrack) | emptyvec
     map = (lcurly form (<whitespace> form)* rcurly) | emptymap
     <lparen> = <'('>
     <rparen> = <')'>
     <lsquarebrack> = <'['>
     <rsquarebrack> = <']'>
     <lcurly> = <'{'>
     <rcurly> = <'}'>
     <token> = word | number | infix-operator | string
     <emptylist> = lparen rparen
     <emptyvec> = lsquarebrack rsquarebrack
     <emptymap> = lcurly rcurly
     whitespace = #'\\s+'
     infix-operator = #'[\\+\\*\\/\\-]+' | 'is' | 'as'
     word = #'[a-zA-Z!?]+'
     string = <quote> #'[a-zA-Z!?10-9]+' <quote>
     quote = '\"'
     number = #'[0-9]+'"))

;; All these functions takes and returns strings
(defn with-indent [code]
  (clojure.string/join
   (map #(str "\t" %) (clojure.string/split code #"\n"))))

(defn assign [variable code]
  (format "%s = %s;" variable code))

(defn define [variable code]
  (format "var %s = %s;" variable code))

(defn infix [op a b]
  (format "%s %s %s" a op b))

(defn fn-call [f args]
  (format "%s(%s)" f args))

(defn fn-def [arglist body]
  (format "function(%s) {\n%s\n}" arglist body))

(defn named-fn-def [fn-name arglist body]
  (format "function %s(%s) {\n%s\n}" fn-name arglist (with-indent body)))

(defn return [code]
  (format "return %s;" code))

;; Pattern matching functions (takes parts of ASTs and generates js-strings)
(declare match-list)
(declare match-args)
(declare match-form)
(declare match-body)

(defn match-list [l]
  (match l
         [[:word "set!"] [:word variable] form] (assign variable (match-form form))
         [[:word "def"] [:word variable] form] (define variable (match-form form))
         [[:infix-operator op] a b] (infix op (match-form a) (match-form b))
         [[:word "fn"] [:vector & args] & body] (fn-def (match-args args) (match-body body))
         [f & args] (fn-call (match-form f) (match-args args))
         :else (str "//Failed to match list " (str l))))

(defn match-args [args]
  (clojure.string/join ", " (map match-form args)))

(defn match-body [body]
  (clojure.string/join ";\n"
                       (concat (map #(with-indent (match-form %)) (butlast body))
                               [(with-indent (return (match-form (last body))))])))


(defn match-form [form]
    (match form
           nil "// Form is nil"
           [:word x] x
           [:number n] n
           [:string s] (str "\"" s "\"")
           [:list & l] (match-list l)
           :else (str "//Failed to match form " (str form))))



;; Putting it all together

(defn tree->js
  "Takes an AST (from instaparse) and returns js code as a string"
  [tree]
  (if (= (class tree) instaparse.gll.Failure)
    tree
    (let [[head & forms] tree]
      (assert (= head :program))
      (let [js-forms (map match-form forms)]
        (clojure.string/join "\n\n" js-forms)))))

(defn lisp->js
  "Convert lisp to js (string -> string)"
  [code]
  (let [tree (p code)]
    (tree->js tree)))












;; (start-watch [{:path "/Users/erik/Desktop"
;;                :event-types [:create :modify :delete]
;;                :bootstrap (fn [path] (println "Starting to watch " path))
;;                :callback (fn [event filename] (println event filename))
;;                :options {:recursive true}}])


