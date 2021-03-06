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
(p "erik ; hej pa dig\nsvej")
(p "hej.hej")

(p "-100")
(p "(2)")
(p "(2 3)")

(p ".-hej")
(p ".hej")

(p ":red")
(p "λ:red")

(p "(yield 50)")


; Code generation
(lisp->js "42")
(lisp->js "100 300")
(lisp->js "-5")
(lisp->js "10.2")
(lisp->js "-500.2323")


(lisp->js "(+ 2 3)")
(lisp->js "(- 2 3)")
(lisp->js "(* 2 3)")
(lisp->js "(/ 2 3)")
(lisp->js "(* 1 (+ 2 3))")
(lisp->js "(as x int)")
(lisp->js "x")
(lisp->js "(* (+ 2 3) 10)")

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
(lisp->js "(fn f [a b] (f a b))")

(lisp->js "[1 2 (f 3 4) 5]")

(lisp->js "(if true 1 2)")
(lisp->js "(if (x 5 6 7) (y) (z))")
(lisp->js "(if (is x Monkey) \"yeah\" \"nope\")")

(lisp->js "(let [a 10 b 20] a)")

(lisp->js "{}")
(lisp->js "{a 3}")
(lisp->js "{a 3 b 4}")

(lisp->js "(get stats x)")
(lisp->js "(set! y (get f \"a\"))")

(lisp->js "(fn void Start [] (pp (range 10 5 2)))")

(lisp->js "(.-hej.san o)")
(lisp->js "(set! (.-transform o) (new Vector3 0 0 0))")

(lisp->js "(fn [a] (max 5 10))")
(lisp->js "#(max % 10)")
(lisp->js "#(max 5 10)")

(lisp->js "(map .-transform transforms)")

(lisp->js "(defn foo [x] (* x x))")

(lisp->js "(def name-with-dashes awesome?!)")
(lisp->js "(defn awesome? [best-guess] (swipe! x))")

(lisp->js "(.foo p)")
(lisp->js "(.Rotate transform 10 20 30)")

(lisp->js "(def Vector3 my-vector nil)")
(lisp->js "(def-static Vector3 my-vector nil)")
(lisp->js "(def-static foo [1 2 3])")

(lisp->js ":red")
(lisp->js "(:green colors)")
(lisp->js "(map λ:age peeps)")

(lisp->js "(def a->b 10)")

(lisp->js "(fn [x] (yield 100) 200)")

(lisp->js "(defmethod x [y] z)")
(lisp->js "(defmethod x [y] (yield 100))")
(lisp->js "(defn x [] 100)")
(lisp->js "(defn x [y] (yield 100))")

