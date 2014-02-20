(defproject unity-lisp "0.1.0"
  :description "Lisp to Unity Script converter"
  :url "http://github.com/eriksvedang/unity-lisp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [instaparse "1.2.15"]
                 [clojure-watch "0.1.9"]
                 [org.clojure/core.match "0.2.1"]]
  :main unity-lisp.core)
