(import Mathf)

(def rot-speed 10.0)

(defvoid Update []
  (.Rotate this.transform (new Vector3 0 (* Time.deltaTime rot-speed) 0))
  (set! transform.localScale.x (+ 1.0 (* 0.5 (Sin Time.time)))))
