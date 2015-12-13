(ns hiccup.def
  "Macros for defining functions that generate HTML"
  (:require [hiccup.core :refer [html]]
            [hiccup.def :as hdef]))

(defmacro defhtml
  "Define a function, but wrap its output in an implicit html macro."
  [name & fdecl]
  (let [[fhead fbody] (split-with #(not (or (list? %) (vector? %))) fdecl)
        wrap-html     (fn [[args & body]] `(~args (html ~@body)))]
    `(defn ~name
       ~@fhead
       ~@(if (vector? (first fbody))
           (wrap-html fbody)
           (map wrap-html fbody)))))

(defn wrap-attrs
  "Add an optional attribute argument to a function that returns a element vector."
  [fbody]
  `([& args#]
    (let [func# (fn ~@fbody)]
      (if (map? (first args#))
        (let [[tag# & body#] (apply func# (rest args#))]
          (if (map? (first body#))
            (apply vector tag# (merge (first body#) (first args#))
                   (rest body#))
            (apply vector tag# (first args#) body#)))
        (apply func# args#)))))

;; Hiccup uses alter-meta! and alter-var-root to define defelem, but at
;; the time of this writing, clojurescript does not have alter-var-root
;; and alter-meta! is buggy.
;; See http://dev.clojure.org/jira/browse/CLJS-1511
(defmacro defelem
  "Defines a function that will return a element vector. If the first argumen
  passed to the resulting function is a map, it merges it with the attribute
  map of the returned element value."
  [name & fdecl]
  (let [[fhead fbody] (split-with #(not (or (list? %) (vector? %))) fdecl)]
    `(defn ~name ~@fhead ~@(wrap-attrs fbody))))
