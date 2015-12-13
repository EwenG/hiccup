(ns hiccup.test.element
  (:require [hiccup.element :refer [javascript-tag link-to mail-to
                                    unordered-list ordered-list]]
            [hiccup.util]
            #?(:clj [clojure.test :refer :all])
            #?(:cljs [cljs.test :refer-macros
                      [deftest is testing run-tests run-all-tests]]))
  (:import #?(:clj [java.net URI] :cljs [goog Uri])))

(deftest javascript-tag-test
  (is (= (javascript-tag "alert('hello');")
         [:script {:type "text/javascript"}
          "//<![CDATA[\nalert('hello');\n//]]>"])))

(deftest link-to-test
  (is (= (link-to "/")
         [:a {:href #?(:clj (URI. "/") :cljs (Uri.parse "/"))} nil]))
  (is (= (link-to "/" "foo")
         [:a {:href #?(:clj (URI. "/") :cljs (Uri.parse "/"))}
          (list "foo")]))
  (is (= (link-to "/" "foo" "bar")
         [:a {:href #?(:clj (URI. "/") :cljs (Uri.parse "/"))}
          (list "foo" "bar")])))

(deftest mail-to-test
  (is (= (mail-to "foo@example.com")
         [:a {:href "mailto:foo@example.com"} "foo@example.com"]))
  (is (= (mail-to "foo@example.com" "foo")
         [:a {:href "mailto:foo@example.com"} "foo"])))

(deftest unordered-list-test
  (is (= (unordered-list ["foo" "bar" "baz"])
         [:ul (list [:li "foo"]
                    [:li "bar"]
                    [:li "baz"])])))

(deftest ordered-list-test
  (is (= (ordered-list ["foo" "bar" "baz"])
         [:ol (list [:li "foo"]
                    [:li "bar"]
                    [:li "baz"])])))
