(defn pos-of-object [name]
  (let [go (GameObject.Find name)]
    (if go
      (.-transform.position go)
      "nil")))
