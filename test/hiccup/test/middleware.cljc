(ns hiccup.test.middleware
  #?(:clj (:require [hiccup.core :refer [html]]
                    [clojure.test :refer :all]
                    [hiccup.middleware :refer [wrap-base-url]]
                    [hiccup.element :refer [link-to]])
     :cljs (:require [cljs.test :refer-macros
                      [deftest is testing run-tests run-all-tests]]
                     [hiccup.core]
                     [hiccup.middleware :refer [wrap-base-url]]
                     [hiccup.element :refer [link-to]]))
  #?(:cljs (:require-macros [hiccup.core :refer [html]])))

(defn test-handler [request]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (html [:html [:body (link-to "/bar" "bar")]])})

(deftest test-wrap-base-url
  (let [resp ((wrap-base-url test-handler "/foo") {})]
    (is (= (:body resp)
           "<html><body><a href=\"/foo/bar\">bar</a></body></html>"))))
