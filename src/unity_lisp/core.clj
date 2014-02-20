(ns unity-lisp.core
  (:require [instaparse.core :as insta]
            [clojure-watch.core :refer [start-watch]]
            [clojure.core.match :refer [match]]))

(def p
  (insta/parser
    "program = (form <whitespace>*)*
     <form> = token | list | vector | map | <comment>
     list = (lparen (<whitespace>* form <whitespace>*)* rparen) | emptylist
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
     word = #'[a-zA-Z!?.]*[a-zA-Z!?.0-9]+'
     string = <quote> #'[a-zA-Z!?10-9]+' <quote>
     quote = '\"'
     comment = #';.*'
     number = #'[0-9]+.*[0-9]*'"))

;; All these functions takes and returns strings
(defn with-indent [code]
  (clojure.string/join
   (map #(str "\t" %) (clojure.string/split code #"\n"))))

(defn assign [variable code]
  (format "%s = %s" variable code))

(defn define [variable code]
  (format "var %s = %s" variable code))

(defn infix [op a b]
  (format "%s %s %s" a op b))

(defn fn-call [f args]
  (format "%s(%s)" f args))

(defn fn-def [arglist body]
  (format "function(%s) {\n%s\n}" arglist body))

(defn named-fn-def [fn-name arglist body]
  (format "function %s(%s) {\n%s\n}" fn-name arglist body))

(defn return [code]
  (format "return %s;" code))

(defn if-statement [conditional body else-body]
  (format "(%s ? %s : %s)" conditional body else-body))

(defn let-statement [bindings body]
  (format "function() {\n%s\n%s\n}();" bindings body))

(defn new-statement [type-name arglist]
  (format "new %s(%s)" type-name arglist))


;; Pattern matching functions (takes parts of ASTs and generates js-strings)
(declare match-list)
(declare match-args)
(declare match-form)
(declare match-body)
(declare match-vector)
(declare match-bindings)
(declare match-map)

(defn match-list [l]
  (match l
         [[:word "set!"] [:word variable] form] (assign variable (match-form form))
         [[:word "def"] [:word variable] form] (define variable (match-form form))
         [[:word "import"] [:word lib]] (str "import " lib)
         [[:word "new"] [:word type-name] & args] (new-statement type-name (match-args args))
         [[:infix-operator op] a b] (infix op (match-form a) (match-form b))
         [[:word "fn"] [:vector & args] & body] (fn-def (match-args args) (match-body body false))
         [[:word "fn"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body false))
         [[:word "void"] [:word fn-name] [:vector & args] & body] (named-fn-def fn-name (match-args args) (match-body body true))
         [[:word "if"] conditional body else-body] (if-statement (match-form conditional) (match-form body) (match-form else-body))
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
  (clojure.string/join ";\n"
                       (concat (map #(with-indent (match-form %)) (butlast body))
                               [(with-indent (if is-void
                                               (str (match-form (last body)) ";")
                                               (return (match-form (last body)))))])))

(defn match-binding [b]
  (match (vec b)
         [[:word variable] form] (assign variable (match-form form))
         :else (str "/* Failed to match binding " b " */")))

(defn match-bindings [bindings]
  (clojure.string/join "\n"
                       (map #(with-indent (match-binding %)) (partition 2 bindings))))

(defn match-form [form]
    (match form
           nil "null"
           [:word "nil"] "null"
           [:word x] x
           [:number n] n
           [:string s] (str "\"" s "\"")
           [:vector & v] (match-vector v)
           [:list & l] (match-list l)
           [:map & m] (match-map m)
           ;[:comment text] (str "")
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
                 :bootstrap (fn [path] (println "Starting to watch " path))
                 :callback on-file-event
                 :options {:recursive true}}]))


