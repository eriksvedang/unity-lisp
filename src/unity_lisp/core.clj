(ns unity-lisp.core
  (:gen-class)
  (:require [watchtower.core :refer [watcher extensions rate ignore-dotfiles file-filter on-change]]
            [unity-lisp.emit :refer :all]
            [unity-lisp.macros :refer :all]
            [unity-lisp.parser :refer :all]
            [unity-lisp.file-helpers :refer :all]))

(def out-folder-name "out")

(defn process-file [path]
  (assert (= (class path) java.lang.String))
  (ensure-folder path out-folder-name)
  (let [js-filename (append-subfolder (clj-to-js-path path) out-folder-name)]
    (->> (slurp path)
         lisp->js
         (str "import core;\n\n")
         ; (str "#pragma strict\n\n")
         (spit js-filename))
    (println "Saved" js-filename)))

(defn process-files [files]
  (let [file-paths (map #(. % getPath) files)]
    (doseq [path file-paths]
      (process-file path))))

(defn watch [path]
  (watcher [path]
           (rate 1000) ; ms
           (file-filter ignore-dotfiles)
           (file-filter (extensions :clj :cljs))
           (on-change process-files))
  (println "Started watching dir for Unity Lisp files:" path))

(defn -main [& args]
  (reset-default-macros!)
  (let [path (if (< 0 (count args)) (first args) "./")]
    (watch path)))

(comment
  ;; Eval this code to start compiling the code in the 'lab' project which
  ;; is used to develop the core library and to make sure that everything
  ;; works in a proper Unity project.
  (watch "./lab/Assets/Code"))
