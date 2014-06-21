(ns unity-lisp.file-helpers)

(defn clj-to-js-path [clj-path]
  (clojure.string/replace clj-path #".clj" ".js"))

(defn ensure-folder [file-path subfolder-name]
  (let [path-segments (clojure.string/split file-path #"/")
        all-except-last (drop-last path-segments)
        sub-path (clojure.string/join "/" all-except-last)
        new-dir-path (str sub-path "/" subfolder-name)]
    (if (.mkdir (java.io.File. new-dir-path))
      (println "Created subfolder at" new-dir-path))
    ))

(defn append-subfolder [file-path subfolder-name]
  (let [path-segments (clojure.string/split file-path #"/")
        last-item (last path-segments)
        all-except-last (drop-last path-segments)
        out-path (clojure.string/join "/" all-except-last)]
    (str out-path "/" subfolder-name "/" last-item)))
