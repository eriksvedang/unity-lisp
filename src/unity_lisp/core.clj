(ns unity-lisp.core
  (:gen-class)
  (:require [instaparse.core :as insta]
            [clojure-watch.core :refer [start-watch]]
            [clojure.core.match :refer [match]]))

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
     <token> = word | number | infix-operator | string | accessor | sugar-lambda | percent-sign
     whitespace = #'\\s+'
     number = #'-*[0-9]+.*[0-9]*'
     infix-operator = (#'[\\+\\*\\/]+' | 'is' | 'as' | '-' | 'and' | '==' | '!=' | '<' | '>' | '<=' | '>=' ) <#'\\s+'>
     accessor = '.-' word
     word = #'[a-zA-Z!?.]*[a-zA-Z!?.0-9-]+'
     string = <quote> #'[a-zA-Z!?10-9 :;]+' <quote>
     quote = '\"'
     comment = #';.*'"))


;; Helpers

(defn with-indent [code]
  (clojure.string/join
   (map #(str "\t" % "\n") (clojure.string/split code #"\n"))))

(defn js-naming [lisp]
  (-> lisp
      (#(if (= \? (last lisp))
          (str "is" (clojure.string/capitalize (apply str (butlast %))))
          %))
      (clojure.string/replace "-" "_")
      (clojure.string/replace "?" "_QMARK")
      (clojure.string/replace "!" "_BANG")))

;; Javscript output, all these functions takes and returns strings

(defn assign [variable code]
  (format "%s = %s" (js-naming variable) code))

(defn define [variable code]
  (format "var %s = %s" (js-naming variable) code))

(defn infix [op a b]
  (format "(%s %s %s)" a op b))

(defn fn-call [f args]
  (format "%s(%s)" (js-naming f) args))

(defn fn-def [arglist body]
  (format "function(%s) : Object {\n%s}" arglist body))

(defn named-fn-def [fn-name arglist body]
  (format "function %s(%s) : Object {\n%s}" (js-naming fn-name) arglist body))

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

(defn new-statement [type-name arglist]
  (format "new %s(%s)" type-name arglist))

(defn nth-statement [l n]
  (format "%s[%s]" l n))

(defn access [attr obj]
  (format "%s.%s" obj attr))

(defn attribute-accessor-fn [attribute]
  (format "function(obj) { return obj.%s; }" attribute))


;; Pattern matching functions (takes parts of ASTs and generates js-strings)
(declare match-list)
(declare match-args)
(declare match-form)
(declare match-body)
(declare match-statement)
(declare match-vector)
(declare match-bindings)
(declare match-map)

(defn match-list [l]
  (match l
         [[:word "set!"] variable form] (assign (match-form variable) (match-form form))
         [[:word "def"] [:word variable] form] (define variable (match-form form))
         [[:accessor ".-" [:word attribute]] obj] (access attribute (match-form obj))
         [[:word "import"] [:word lib]] (str "import " lib)
         [[:word "nth"] form index-form] (nth-statement (match-form form) (match-form index-form))
         [[:word "new"] [:word type-name] & args] (new-statement type-name (match-args args))
         [[:infix-operator op] a b] (infix op (match-form a) (match-form b))
         [[:word "fn"] [:vector & args] & body] (fn-def (match-args args) (match-body body false))
         [[:word "fn"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body false))
         [[:word "fn"] [:word "void"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body true))
         [[:word "defn"] [:word fn-name] [:vector & args] & body] (static-named-fn-def fn-name (match-args args) (match-body body false))
         [[:word "void"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body true))
         [[:word "if"] conditional body else-body] (if-statement (match-form conditional) (match-form body) (match-form else-body))
         [[:word "do-if"] conditional body else-body] (do-if-statement (match-form conditional) (match-statement body) (match-statement else-body))
         [[:word "let"] [:vector & bindings] & body] (let-statement (match-bindings bindings) (match-body body false))
         [f & args] (fn-call (match-form f) (match-args args))
         :else (str "/* Failed to match list " (str l) "*/")))

(defn match-vector [v]
  (format "[%s]" (clojure.string/join ", " (map match-form v))))

(defn match-map [m]
  (format "{%s}" (clojure.string/join ", " (map #(clojure.string/join ": " %) (partition 2 (map match-form m))))))

(defn match-args [args]
  (clojure.string/join ", " (map match-form args)))

(defn match-body [body is-void]
  (clojure.string/join
                       (concat (map #(with-indent (str (match-form %) ";")) (butlast body))
                               [(with-indent (let [last-statement (str (match-form (last body)) ";")]
                                               (if is-void
                                                 last-statement
                                                 (return last-statement))))])))

(defn match-statement [form]
  (println form)
  (with-indent (str (match-form form) ";")))

(defn match-binding [b]
  (match (vec b)
         [[:word variable] form] (define variable (match-form form))
         :else (str "/* Failed to match binding " b " */")))

(defn match-bindings [bindings]
  (clojure.string/join (map #(with-indent (str (match-binding %) ";")) (partition 2 bindings))))

(defn match-infix [op]
  (case op
    "+" "_add_fn"
    "-" "_sub_fn"
    "*" "_mul_fn"
    "/" "_div_fn"))

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
           [:sugar-lambda body] (fn-def "__ARG__" (match-body [body] false))
           [:percent-sign "%"] "__ARG__"
           [:accessor ".-" [:word attribute]] (attribute-accessor-fn attribute)
           :else (str "/* Failed to match form " form " */")))



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

(defn process-path [path]
  (->> (slurp path)
      lisp->js
      (str "import core;\n\n")
      (spit (clj-to-js-path path))))

(defn clj? [path]
  (re-find #".clj" path))

(defn on-file-event [event path]
  (println event path)
  (if (or (= event :create) (= event :modify))
    (if (clj? path)
      (process-path path)
      (println "Not a .clj file"))))

(defn watch [path]
  (start-watch [{:path path
                 :event-types [:create :modify :delete]
                 :bootstrap (fn [path] (println "Starting to watch" path))
                 :callback on-file-event
                 :options {:recursive true}}]))

(defn -main [& args]
  (let [path (if (< 0 (count args)) (first args) "./")]
    (watch path)))
