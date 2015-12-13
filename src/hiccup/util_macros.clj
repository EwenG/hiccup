(ns hiccup.util-macros
  "Utility functions for Hiccup."
  (:require [clojure.string :as str])
  (:import java.net.URI
           java.net.URLEncoder))

(defmacro with-base-url
  "Sets a base URL that will be prepended onto relative URIs. Note that for this
  to work correctly, it needs to be placed outside the html macro."
  [base-url & body]
  `(binding [hiccup.util/*base-url* ~base-url]
     ~@body))

(defmacro with-encoding
  "Sets a default encoding for URL encoding strings. Defaults to UTF-8."
  [encoding & body]
  `(binding [hiccup.util/*encoding* ~encoding]
     ~@body))
