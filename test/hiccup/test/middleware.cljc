(ns hiccup.test.middleware
  (:require [hiccup.core #?(:clj :refer :cljs :refer-macros) [html]]
            [hiccup.middleware :refer [wrap-base-url]]
            [hiccup.element :refer [link-to]]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros
                      [deftest is testing run-tests run-all-tests]])))

(defn test-handler [request]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (html [:html [:body (link-to "/bar" "bar")]])})

(deftest test-wrap-base-url
  (let [resp ((wrap-base-url test-handler "/foo") {})]
    (is (= (:body resp)
           "<html><body><a href=\"/foo/bar\">bar</a></body></html>"))))
