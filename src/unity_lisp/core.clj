(ns unity-lisp.core
  (:gen-class)
  (:require [instaparse.core :as insta]
            [clojure.core.match :refer [match]]
            [watchtower.core :refer [watcher extensions rate ignore-dotfiles file-filter on-change]]))

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
     <token> = word | number | infix-operator | string | accessor | method | sugar-lambda | percent-sign | keyword | keyword-fn | yield
     whitespace = #'\\s+'
     number = #'-*[0-9]+\\.?[0-9]*'
     infix-operator = (#'[\\+\\*\\/]+' | 'is' | 'as' | '-' | 'and' | '==' | '!=' | '<' | '>' | '<=' | '>=' ) <#'\\s+'>
     accessor = '.-' word
     method = '.' word
     yield = 'yield'
     word = #'[a-zA-Z!?]+[a-zA-Z!?.0-9-<>]*'
     string = <quote> #'[a-zA-Z!?10-9 :;]+' <quote>
     quote = '\"'
     keyword = <':'> word
     keyword-fn = <'λ'> token
     comment = #';.*'"))


;; Js generator helpers

(defn with-indent [code]
  (clojure.string/join
   (map #(str "\t" % "\n") (clojure.string/split code #"\n"))))

(defn js-naming [lisp]
  (-> lisp
      (#(if (= \? (last lisp))
          (str "is" (clojure.string/capitalize (apply str (butlast %))))
          %))
      (clojure.string/replace "-" "_")
      (clojure.string/replace "<" "_LT")
      (clojure.string/replace ">" "_GT")
      (clojure.string/replace "?" "_QMARK")
      (clojure.string/replace "!" "_BANG")))

;; Javscript output, all these functions takes and returns strings

(defn assign [variable code]
  (format "%s = %s" (js-naming variable) code))

(defn define [variable code]
  (format "var %s = %s" (js-naming variable) code))

(defn define-static [variable code]
  (str "static " (define variable code)))

(defn define-typed [type-name variable code]
  (format "var %s : %s = %s" (js-naming variable) type-name code))

(defn define-typed-static [type-name variable code]
  (str "static " (define-typed type-name variable code)))

(defn infix [op a b]
  (format "(%s %s %s)" a op b))

(defn fn-call [f args]
  (format "%s(%s)" (js-naming f) args))

(defn method-call [method-name obj args]
  (format "%s.%s(%s)" obj method-name args))

(defn fn-def [return-type arglist body]
  (format "function(%s) : %s {\n%s}" arglist return-type body))

(defn named-fn-def [fn-name return-type arglist body]
  (format "function %s(%s) : %s {\n%s}" (js-naming fn-name) arglist return-type body))

(defn static-named-fn-def [fn-name arglist body]
  (format "static function %s(%s) : Object {\n%s}" (js-naming fn-name) arglist body))

(defn return [code]
  (format "return %s" code))

(defn if-statement [conditional body else-body]
  (format "(%s ? %s : %s)" conditional body else-body))

(defn do-if-statement [conditional body else-body]
  (format "if(%s) {\n%s} else {\n%s}" conditional body else-body))

(defn let-statement [bindings body]
  (format "function() : Object {\n%s%s}()" bindings body))

(defn do-statement [body]
  (format "function() : Object {\n%s}()" body))

(defn new-statement [type-name arglist]
  (format "new %s(%s)" type-name arglist))

(defn nth-statement [l n]
  (format "%s[%s]" l n))

(defn access [attr obj]
  (format "%s.%s" obj attr))

(defn attribute-accessor-fn [attribute]
  (format "function(__OBJ__) { return __OBJ__.%s; }" attribute))

(defn keyword-access [keyword-name obj]
  (format "%s[%s]" obj (str "\"" keyword-name "\"")))

(defn keyword-fn [keyword-name]
  (format "function(__MAP__) { return __MAP__[%s]; }" keyword-name))

(defn lone-method-call [method-name]
  (format "function(__OBJ__) { return __OBJ__.%s(); }" method-name))

(defn deftype-statement [class-name members body]
  (format "public class %s {\n%s}" class-name body))

(defn update-statement [obj f]
  (format "%s = %s(%s)" obj f obj))




(defn has-x?
  "Does the form 'body' contain x anywhere in it?"
  [body k]
  (seq (filter #(= k %) (flatten body))))

;; Pattern matching functions (takes parts of ASTs and generates js-strings)
(declare match-list)
(declare match-args)
(declare match-form)
(declare match-body)
(declare match-class-body)
(declare match-statement)
(declare match-vector)
(declare match-bindings)
(declare match-map)
(declare match-method)
(declare match-do-statement)
(declare match-fn-def)
(declare match-method-def)

(defn match-list [l]
  (match l
         [[:word "def"] [:word variable] form] (define variable (match-form form))
         [[:word "def"] [:word type-name] [:word variable] form] (define-typed type-name variable (match-form form))
         [[:word "def-static"] [:word variable] form] (define-static variable (match-form form))
         [[:word "def-static"] [:word type-name] [:word variable] form] (define-typed-static type-name variable (match-form form))

         [[:word "set!"] variable form] (assign (match-form variable) (match-form form))

         [[:word "import"] [:word lib]] (str "import " lib)
         [[:word "not"] form] (str "!(" (match-form form) ")")
         [[:yield "yield"] form] (str "yield " (match-form form))
         [[:word "update!"] form f] (update-statement (match-form form) (match-form f))
         [[:word "nth"] form index-form] (nth-statement (match-form form) (match-form index-form))
         [[:word "new"] [:word type-name] & args] (new-statement type-name (match-args args))
         [[:word "do"] & forms] (match-do-statement forms)
         [[:word "let"] [:vector & bindings] & body] (let-statement (match-bindings bindings) (match-body body false))
         [[:word "deftype"] [:word class-name] [:vector & members] & body] (deftype-statement class-name members (match-class-body body))

         [[:word "fn"] [:vector & args] & body] (match-fn-def args body)
         [[:word "defn"] [:word fn-name] [:vector & args] & body] (static-named-fn-def fn-name (match-args args) (match-body body false))
         [[:word "defmethod"] [:word fn-name] [:vector & args] & body] (match-method-def fn-name args body)

;         [[:word "fn"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body false))
;         [[:word "void"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body true))
;         [[:word "fn"] [:word "void"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body true))

         [[:word "if"] conditional body else-body] (if-statement (match-form conditional) (match-form body) (match-form else-body))
         [[:word "do-if"] conditional body else-body] (do-if-statement (match-form conditional) (match-statement body) (match-statement else-body))

         [[:method "." [:word method-name]] obj & args] (method-call method-name (match-form obj) (match-args args))
         [[:accessor ".-" [:word attribute]] obj] (access attribute (match-form obj))

         [[:infix-operator op] a b] (infix op (match-form a) (match-form b))
         [[:keyword [:word keyword-name]] obj] (keyword-access keyword-name (match-form obj))

         [f & args] (fn-call (match-form f) (match-args args))
         :else (str " /* Failed to match list " (str l) " */ ")))

(defn match-do-statement [forms]
  (do-statement (match-body forms true)))

(defn match-vector [v]
  (format "[%s]" (clojure.string/join ", " (map match-form v))))

(defn match-map [m]
  (format "{%s}" (clojure.string/join ", " (map #(clojure.string/join ": " %) (partition 2 (map match-form m))))))

(defn match-args [args]
  (clojure.string/join ", " (map match-form args)))

(defn match-body [body is-void]
  (clojure.string/join (concat (map #(with-indent (str (match-form %) ";")) (butlast body))
                               [(with-indent (let [last-statement (str (match-form (last body)) ";")]
                                               (if is-void
                                                 last-statement
                                                 (return last-statement))))])))

(defn match-class-body [body]
  (clojure.string/join (concat (map #(with-indent (str (match-form %) ";")) body))))

(defn match-method-def [fn-name args body]
  (if (has-x? body :yield)
    (named-fn-def fn-name "IEnumerator" (match-args args) (match-body body true))
    (named-fn-def fn-name "Object" (match-args args) (match-body body false))))

(defn match-fn-def [args body]
  (if (has-x? body :yield)
    (fn-def "IEnumerator" (match-args args) (match-body body true))
    (fn-def "Object" (match-args args) (match-body body false))))

(defn match-statement [form]
  (println form)
  (with-indent (str (match-form form) ";")))

(defn match-binding [b]
  (match (vec b)
         [[:word variable] form] (define variable (match-form form))
         :else (str " /* Failed to match binding " b " */ ")))

(defn match-bindings [bindings]
  (clojure.string/join (map #(with-indent (str (match-binding %) ";")) (partition 2 bindings))))

(defn match-sugar-lambda [body]
  (if (has-x? body :percent-sign)
    (fn-def "Object" "__ARG__" (match-body [body] false))
    (fn-def "Object" "" (match-body [body] false))))

(filter #(= :list %) (flatten [:list [:word "max"] [:number "5"] [:number "10"]]))
;(flatten [[[1 2 3][4 [5 6]] 7 8] 0])

(defn match-infix [op]
  (case op
    "+" "_add_fn"
    "-" "_sub_fn"
    "*" "_mul_fn"
    "/" "_div_fn"
    "<" "_less_than_fn"
    ">" "_greater_than_fn"))

(defn match-method [])

(defn match-form [form]
    (match form
           nil "/* nothingness */"
           [:word "nil"] "null"
           [:word x] (js-naming x)
           [:number n] n
           [:string s] (str "\"" s "\"")
           [:vector & v] (match-vector v)
           [:list & l] (match-list l)
           [:map & m] (match-map m)
           [:infix-operator op] (match-infix op)
           [:sugar-lambda body] (match-sugar-lambda body)
           [:percent-sign "%"] "__ARG__"
           [:accessor ".-" [:word attribute]] (attribute-accessor-fn attribute)
           [:keyword [:word keyword-name]] (str "\"" keyword-name "\"")
           [:keyword-fn k] (keyword-fn (match-form k))
           [:method "." [:word method-name]] (lone-method-call method-name)
           :else (str " /* Failed to match form " form " */ ")))



;; Putting it all together

(defn tree->js
  "Takes an AST (from instaparse) and returns js code as a string"
  [tree]
  (if (= (class tree) instaparse.gll.Failure)
    (str "/*\n" (pr-str tree) "\n*/")
    (let [[head & forms] tree]
      (assert (= head :program))
      (let [js-forms (map match-form forms)
            with-semicolons (map #(str % ";") js-forms)]
        (clojure.string/join "\n\n" with-semicolons)))))

(defn lisp->js
  "Convert lisp to js (string -> string)"
  [code]
  (let [tree (p code)]
    (tree->js tree)))



;; File watcher

(defn clj-to-js-path [clj-path]
  (clojure.string/replace clj-path #".clj" ".js"))

(defn process-file [path]
  (let [js-filename (clj-to-js-path path)]
    (->> (slurp path)
         lisp->js
         (str "import core;\n\n")
         (spit js-filename))
    (println "Saved" js-filename)))

(defn process-files [files]
  (doseq [f files]
    (process-file f)))

(defn watch [path]
  (watcher [path]
           (rate 1000) ; ms
           (file-filter ignore-dotfiles)
           (file-filter (extensions :clj :cljs))
           (on-change process-files)))

(defn -main [& args]
  (let [path (if (< 0 (count args)) (first args) "./")]
    (watch path)))
