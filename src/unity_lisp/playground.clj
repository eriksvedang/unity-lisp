(use 'unity-lisp.core)

(defn spit-and-print [s]
  (do
    (spit "out.js" s)
    s))

(comment
  (watch "/Users/erik/Documents/UnityLisp/UnityLispUnity/Assets/Lisp"))

(let [input (slurp "./src/unity_lisp/temp.clj")]
  (spit-and-print (lisp->js input)))



; Compile keywords to functions possibly?

; Add:
; loop


; Functions:
; set-nth, mapcat, filter, remove, loop/recur, count
; concat, fnil, keys, vals, not, not=, get-in,
; first, rest, apply (how?),


; Macros:
; -> ->> if-let when do-when



; Cool code that should work:

(take 5 (iterate #(* 2 %) 10))
(take 3 (repeatedly #(rand-int 10)))





