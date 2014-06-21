(def ^GameObject prefab)

(defvoid Start []
  (create-stuff))

(defmethod create-stuff []
  (let [positions [(v3 -1 0 -1) (v3 1 0 -1)
                   (v3 -1 0 1) (v3 1 0 1)
                   (v3 0 1 0) (v3 0 -1 0)]]
    (doall (map #(make-go prefab %) positions))))

(defmethod make-go [prefab pos]
  (GameObject.Instantiate prefab (+ pos transform.position) Quaternion.identity))
