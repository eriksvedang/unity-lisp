(def ^Color original-color)
(def ^Color hover-color Color.red)

(defvoid Start []
  (set! original-color this.renderer.material.color))

(defvoid OnMouseEnter []
  (set! this.renderer.material.color hover-color))

(defvoid OnMouseExit []
  (set! this.renderer.material.color original-color))
