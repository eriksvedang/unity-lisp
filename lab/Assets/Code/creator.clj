(def ^GameObject prefab)

(defvoid Start []
  (create-stuff))

(defmethod create-stuff []
  (let [positions [(v3 -1 0 -1) (v3 1 0 -1)
                   (v3 -1 0 1) (v3 1 0 1)
                   (v3 0 1 0) (v3 0 -1 0)]]
    (doall (map #(make-go prefab 0.5 %) positions))))

(defmethod make-go [prefab distance pos]
  (let [instance (GameObject.Instantiate prefab (+ (* pos distance) transform.position) Quaternion.identity)]
    (set! instance.transform.parent this.transform)
    instance))

