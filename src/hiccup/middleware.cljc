(ns hiccup.middleware
  "Ring middleware functions for Hiccup."
  #?(:clj (:require [hiccup.util-macros :refer [with-base-url]]))
  #?(:cljs (:require-macros [hiccup.util-macros :refer [with-base-url]])))

(defn wrap-base-url
  "Ring middleware that wraps the handler in the with-base-url function. The
  base URL may be specified as an argument. Otherwise, the :context key on the
  request map is used."
  [handler & [base-url]]
  (fn [request]
    (with-base-url (or base-url (:context request))
      (handler request))))
