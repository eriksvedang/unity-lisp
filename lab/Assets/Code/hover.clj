(def ^Color original-color)

(defvoid Start []
  (set! original-color this.renderer.material.color))

(defvoid OnMouseEnter []
  (set! this.renderer.material.color Color.red))

(defvoid OnMouseExit []
  (set! this.renderer.material.color original-color))
