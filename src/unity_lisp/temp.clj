[[:word "fn"] arglist body] (function (parse-args arglist)
                                      (parse-body body))
[[:word "fn"] [:word fn-name] arglist body] (named-function fn-name
                                                            (parse-args arglist)
                                                            (parse-body body))
[[:word "let"] [:vector & bindings] body] (let-bind bindings body)
[[:word "if"] conditional body else-body] (if-statement conditional body else-body)










(defn bind [bindings]
  (let [pairs (partition 2 bindings)]
    (apply str
           (for [[v form] pairs]
             (str "var " (second v) " = " (form->js form) ";\n")))))

(defn parse-args [arglist]
  (let [[head & args] arglist]
    (clojure.string/join ", " (map second args))))

(defn parse-body [body]
  (clojure.string/join "\n" (map form->js body)))

(defn let-bind [bindings body]
  (str "function() {\n"
       (with-indent (bind bindings))
       "\n"
       (with-indent (parse-body body))
       "\n"
       "}();"))

(defn if-statement [conditionals body else-body]
  (str "if(" (form->js conditionals) ") {\n"
       (with-indent (parse-body body)) "\n"
       "} else {\n"
       (with-indent (parse-body else-body)) "\n}"))