(use 'unity-lisp.core)


;(slurp "/Users/erik/Documents/UnityLisp/UnityLispUnity/Assets/Lisp/hej.clj")

(comment
 (process-file "/Users/erik/Documents/UnityLisp/UnityLispUnity/Assets/Lisp/hej.clj"))

(comment
  (watch "/Users/erik/Documents/UnityLisp/UnityLispUnity/Assets/Lisp"))


(reset-default-macros!)
(lisp->js "(PI)")

(lisp->js "(defmacro FI [] (print x))")
(lisp->js "(FI)")

macros



; Language features:

; loop
; for comprehensions
; macros
; -> ->> if-let when do-when


; Other niceties:

; make whitespace match between clj and js-files
; line numbers in js-file
; Be able to use [2 3 4] as a Vector3 !!!


; Known bugs:
;


; Functions:

; set-nth, mapcat, filter, remove, loop/recur, count
; concat, fnil, keys, vals, not, not=, get-in,
; first, rest, apply (how?),



; Cool code that should work:

(take 5 (iterate #(* 2 %) 10))
(take 3 (repeatedly #(rand-int 10)))




