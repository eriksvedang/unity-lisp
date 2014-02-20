(use 'unity-lisp.core)

;; Grammar
(p "")
(p "beta")
(p "beta gamma")

(p "20")
(p "1 2 3")

(p "()")
(p "(1)")
(p "(1 2)")
(p "(1 2 3)")
(p "(- x y)")

(p "(1 2) (3 4)")
(p "(+ 2 3)")

(p "[1 2 3]")
(p "\"erik\"")

(p "{a 10}")




; Code generation
(lisp->js "42")
(lisp->js "100 300")

(lisp->js "(+ 2 3)")
(lisp->js "(- 2 3)")
(lisp->js "(* 2 3)")
(lisp->js "(/ 2 3)")
(lisp->js "(is x int)")
(lisp->js "(as x int)")
(lisp->js "x")

(lisp->js "(foo 1 2 3 4)")
(lisp->js "(* (+ a b) (+ c d))")
(lisp->js "(f 5 10 (g 2))")
(lisp->js "(print \"100\")")
(lisp->js "(+ (f 10) (g 20 30))")

(lisp->js "(def y 10)")
(lisp->js "(set! x 100)")
(lisp->js "(set! a (* (+ a b) (+ c d)))")

(lisp->js "(fn [a] a b)")
(lisp->js "(fn [x y z] (- x y))")
(lisp->js "(fn f [a b] (let [x a y (* b b)] (+ x y)))")

(lisp->js "(if true 1 2)")
(lisp->js "(if (x is Monkey) \"yeah\" \"nope\")")
(lisp->js "(let [a 10 b 20] a)")



(defn spit-and-print [s]
  (do
    (spit "out.js" s)
    s))

;(spit-and-print (lisp->js "(fn [a] a b)"))
(spit-and-print (lisp->js "(fn [x y z] (- x y))"))
;(spit-and-print  (lisp->js "(let [a 10 b 20] a)"))
;(spit-and-print  (lisp->js "(def a (fn [x] (* 100 x)))"))
;(spit-and-print  (lisp->js "(if true 1 (if false 2 3))"))
;(spit-and-print  (lisp->js "(let [a 10] a)"))
;(spit-and-print  (lisp->js "(fn f [a b] (let [x a y (* b b)] (+ x y)))"))
;(spit-and-print  (lisp->js "(fn foo [] (print \"hello\"))"))





















