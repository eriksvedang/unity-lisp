(ns unity-lisp.core-test
  (:require [clojure.test :refer :all]
            [unity-lisp.core :refer :all]))

;;; Parsing

(deftest parse-empty
  (is (= (p "") [:program])))

(deftest parse-symbol
  (is (= (p "beta")
         [:program [:word "beta"]])))

(deftest parse-two-symbols
  (is (= (p "beta gamma")
         [:program [:word "beta"] [:word "gamma"]])))

(deftest parse-numbers
  (is (= (p "1 2 3")
         [:program [:number "1"] [:number "2"] [:number "3"]])))

(deftest parse-empty-list
  (is (= (p "")
         )))

(deftest parse-list-1-element
  (is (= (p "(1)")
         [:program [:list [:number "1"]]])))

(deftest parse-list-3-elements
  (is (= (p "(1 2 3)")
         [:program [:list [:number "1"] [:number "2"] [:number "3"]]])))

(deftest parse-fn-call
  (is (= (p "(f x y)")
         [:program [:list [:word "f"] [:word "x"] [:word "y"]]])))

(deftest parse-two-lists
  (is (= (p "(1 2) (3 4)")
         [:program [:list [:number "1"] [:number "2"]] [:list [:number "3"] [:number "4"]]])))

(deftest parse-vector
  (is (= (p "[1 2 3]")
         [:program [:vector [:number "1"] [:number "2"] [:number "3"]]])))

(deftest parse-string
  (is (= (p "\"erik\"")
         [:program [:string "erik"]])))

(deftest parse-string-with-punctuation
  (is (= (p "\".,?!;:\"")
         [:program [:string ".,?!;:"]])))

(deftest parse-empty-string
  (is (= (p "\"\"") [:program [:string ""]])))

(deftest parse-map
  (is (= (p "{a 10}")
         [:program [:map [:word "a"] [:number "10"]]])))

(deftest parse-whitespace
  (is (= (p "erik ; hej pa dig\nsvej")
         [:program [:word "erik"] [:word "svej"]])))

(deftest parse-string-with-dot
  (is (= (p "hej.hej")
         [:program [:word "hej.hej"]])))

(deftest parse-negative-number
  (is (= (p "-100")
         [:program [:number "-100"]])))

(deftest parse-accessor
  (is (= (p ".-hej")
         [:program [:accessor ".-" [:word "hej"]]])))

(deftest parse-keyword
  (is (= (p ":red")
         [:program [:keyword [:word "red"]]])))

(deftest parse-yield
  (is (= (p "(yield 50)")
         [:program [:list [:yield "yield"] [:number "50"]]])))

(deftest parse-hint
  (is (= (p "^float x")
         [:program [:hint [:word "float"] [:word "x"]]])))

;;; Code generation


;; Constants

(deftest generate-null
  (is (= (lisp->js "nil")
         "null;")))

(deftest generate-number-constant
  (is (= (lisp->js "42")
         "42;")))

(deftest generate-two-number-constants
  (is (= (lisp->js "100 300")
         "100;\n\n300;")))

(deftest generate-negative-number-constant
  (is (= (lisp->js "-5")
         "-5;")))

(deftest generate-float-constant
  (is (= (lisp->js "10.2")
         "10.2;")))

(deftest generate-negative-float-constant
  (is (= (lisp->js "-500.2323")
         "-500.2323;")))


;; Binary operators

(deftest generate-binary-add
  (is (= (lisp->js "(+ 2 3)")
         "(2 + 3);")))

(deftest generate-binary-sub
  (is (= (lisp->js "(- 2 3)")
         "(2 - 3);")))

(deftest generate-binary-mul
  (is (= (lisp->js "(* 2 3)")
         "(2 * 3);")))

(deftest generate-binary-div
  (is (= (lisp->js "(/ 2 3)")
         "(2 / 3);")))

(deftest generate-nested-math
  (is (= (lisp->js "(* (- 10 5) (+ 2 3))")
         "((10 - 5) * (2 + 3));")))

(deftest generate-casting
  (is (= (lisp->js "(as x int)")
         "(x as int);")))


;; Function calls

(deftest generate-fn-call
  (is (= (lisp->js "(foo 1 2 3 4)")
         "foo(1, 2, 3, 4);")))

(deftest generate-nested-fn-call
  (is (= (lisp->js "(f 5 10 (g 20 30))")
         "f(5, 10, g(20, 30));")))

(deftest generate-print-call
  (is (= (lisp->js "(print 100)")
         "print(100);")))


;; Variables, bindings and mutation

(deftest generate-define-var
  (is (= (lisp->js "(def y 10)")
         "var y = 10;")))

(deftest generate-var-with-dashes-in-name
  (is (= (lisp->js "(def name-with-dashes awesome?!)")
         "var name_with_dashes = awesome_QMARK_BANG;")))

(deftest generate-set-var
  (is (= (lisp->js "(set! x 100)")
         "x = 100;")))

(deftest generate-set-var-with-fn-result
  (is (= (lisp->js "(set! a (* (+ a b) (+ c d)))")
         "a = ((a + b) * (c + d));")))

(deftest generate-let-bindings
  (is (= (lisp->js "(let [a 10 b 20] a)")
         "function() : Object {/*let*/\n\tvar a = 10;\n\tvar b = 20;\n\treturn a;\n}();")))

(deftest generate-var-with-type
  (is (= (lisp->js "(def ^Vector3 my-vector nil)")
         "var my_vector : Vector3 = null;")))

(deftest generate-static-var
  (is (= (lisp->js "(def-static foo [1 2 3])")
         "static var foo = [1, 2, 3];")))

(deftest generate-static-var-with-type
  (is (= (lisp->js "(def-static Vector3 my-vector nil)")
         "static var my_vector : Vector3 = null;")))

(deftest generate-use-GT-as-var-name
  (is (= (lisp->js "(def a->b 10)")
         "var a__GTb = 10;")))

(deftest generate-use-LT-as-var-name
  (is (= (lisp->js "(def a<-b 10)")
         "var a_LT_b = 10;")))


;; Defining functions and lambdas

(deftest generate-method
  (is (= (lisp->js "(defmethod x [y] z)")
         "function x(y) : Object {\n\treturn z;\n};")))

(deftest generate-anonymous-function
  (is (= (lisp->js "(fn [a] a b)")
         "function(a) : Object {\n\ta;\n\treturn b;\n};")))

(deftest generate-function-harder
  (is (= (lisp->js "(fn [x y z] (- x y))")
         "function(x, y, z) : Object {\n\treturn (x - y);\n};")))

(deftest generate-sugar-lambda-with-parameter
  (is (= (lisp->js "#(max % 10)")
         "function(__ARG__) : Object {\n\treturn max(__ARG__, 10);\n};")))

(deftest generate-sugar-lambda-no-parameter
  (is (= (lisp->js "#(max 5 10)")
         "function() : Object {\n\treturn max(5, 10);\n};")))

;; TODO: fix!
;; (deftest generate-named-function
;;   (is (= (lisp->js "(fn f [a b] (f a b))")
;;          "")))

(deftest generate-define-function-no-args
  (is (= (lisp->js "(defn x [] 100)")
         "static function x() : Object {\n\treturn 100;\n};")))

(deftest generate-define-function-one-arg
  (is (= (lisp->js "(defn foo [x] (* x x))")
         "static function foo(x) : Object {\n\treturn (x * x);\n};")))

(deftest generate-define-function-with-question-mark
  (is (= (lisp->js "(defn awesome? [best-guess] (swipe! x))")
         "static function isAwesome(best_guess) : Object {\n\treturn swipe_BANG(x);\n};")))

;; If-expressions

(deftest generate-if-expression
  (is (= (lisp->js "(if x 1 2)")
         "(x ? 1 : 2);")))

(deftest generate-if-expression-with-fn-calls
  (is (= (lisp->js "(if (x 5 6 7) (y) (z))")
         "(x(5, 6, 7) ? y() : z());")))

(deftest generate-if-expression-tricky
  (is (= (lisp->js "(if (is x Monkey) \"yeah\" \"nope\")")
         "((x is Monkey) ? \"yeah\" : \"nope\");")))


;; Vectors / lists

(deftest generate-vector-with-value-from-nested-fn-call
  (is (= (lisp->js "[1 2 (f 3 4) 5]")
         "[1, 2, f(3, 4), 5];")))

(deftest generate-vector-access
  (is (= (lisp->js "(nth x 3)")
         "x[3];")))


;; Maps

(deftest generate-empty-map
  (is (= (lisp->js "{}")
         "{};")))

(deftest generate-one-element-map
  (is (= (lisp->js "{a 3}")
         "{a: 3};")))

(deftest generate-two-element-map
  (is (= (lisp->js "{a 3 b 4}")
         "{a: 3, b: 4};")))


;; Member variables and methods access

(deftest generate-member-accessor
  (is (= (lisp->js "(.-hej.san o)")
         "o.hej.san;")))

(deftest generate-member-mutator
  (is (= (lisp->js "(set! (.-transform o) (new Vector3 0 0 0))")
         "o.transform = new Vector3(0, 0, 0);")))

(deftest generate-map-over-member
  (is (= (lisp->js "(map .-transform transforms)")
         "map(function(__OBJ__) { return __OBJ__.transform; }, transforms);")))

(deftest generate-call-method
  (is (= (lisp->js "(.foo p)")
         "p.foo();")))

(deftest generate-call-method-with-args
  (is (= (lisp->js "(.Rotate transform 10 20 30)")
         "transform.Rotate(10, 20, 30);")))


;; Keywords

(deftest generate-keyword
  (is (= (lisp->js ":red")
         "\"red\";")))

(deftest generate-keyword-lookup
  (is (= (lisp->js "(:green colors)")
         "colors[\"green\"];")))

(deftest generate-lookup-keyword-fn
  (is (= (lisp->js "λ:age")
         "function(__MAP__) { return __MAP__[\"age\"]; };")))

(deftest generate-map-keyword-over-map
  (is (= (lisp->js "(map λ:age peeps)")
         "map(function(__MAP__) { return __MAP__[\"age\"]; }, peeps);")))


;; Yield

(deftest generate-anonymous-function-yielding-value
  (is (= (lisp->js "(fn [x] (yield 100) 200)")
         "function(x) : IEnumerator {\n\tyield 100;\n\t200;\n};")))

(deftest generate-method-with-yield
  (is (= (lisp->js "(defmethod x [y] (yield 100))")
         "function x(y) : IEnumerator {\n\tyield 100;\n};")))

(deftest generate-define-function-with-yield
  (is (= (lisp->js "(defn x [y] (yield 100))")
         "static function x(y) : IEnumerator {\n\tyield 100;\n};")))

