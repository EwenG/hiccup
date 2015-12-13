(ns hiccup.form-macros
  "Functions for generating HTML forms and input fields."
  (:require [hiccup.util :refer [as-str]]
            [hiccup.form :refer [*group*]]))

(defmacro with-group
  "Group together a set of related form fields for use with the Ring
  nested-params middleware."
  [group & body]
  `(binding [*group* (conj *group* (as-str ~group))]
     (list ~@body)))
