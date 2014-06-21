
(defvoid Start []
  (pp "Reduce the values 0 - 9 using addition => " (reduce + (range 1 10)))
  (pp "Assoc a value to the map {:name 'Erik' :age 27} => " (assoc {:name :erik :age 27} :city "Gothenburg"))
  (pp "Update a value inside the map {:a 10 :b 20} => " (update-in {:a 10 :b 20} [:b] inc))
  (pp "Add something to the end of a vector [1 2 3] => " (conj [1 2 3] 4))
  (pp "Add something to the end of a range => " (conj (range 10 20) "!!!"))
  (let [x {:a 10 :b {:c 30 :d 40}}]
    (update-in! x [:b :c] #(* -1 %))
    (pp "Destructive update of a map => " x))
  )

