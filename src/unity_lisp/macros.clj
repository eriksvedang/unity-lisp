(ns unity-lisp.macros)

(def macros (atom {}))

(defn clear-macros! []
  (reset! macros []))

(defn add-macro! [macro-name macro-args macro-body]
  (swap! macros assoc macro-name {:args macro-args :body macro-body}))

(defn get-macro [macro-name]
  (get @macros macro-name))

(defn reset-default-macros! []
  (add-macro! "PI" [] [:number "42"]))

