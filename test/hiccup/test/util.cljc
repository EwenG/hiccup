(ns hiccup.test.util
  #?@(:clj [(:require [hiccup.core :refer [html]]
                      [clojure.test :refer :all]
                      [hiccup.util-macros :refer :all]
                      [hiccup.util :refer :all])
            (:import java.net.URI)]
     :cljs [(:require [cljs.test :refer-macros
                       [deftest is testing run-tests run-all-tests are]]
                      [hiccup.util :refer [escape-html *html-mode* url
                                           as-str to-str to-uri
                                           url-encode]])
            (:require-macros [hiccup.util-macros :refer [with-base-url
                                                         with-encoding]])
            (:import [goog Uri])]))

(defn make-uri [s]
  #?(:clj (URI. s)
     :cljs (Uri.parse s)))

(deftest test-escaped-chars
  (is (= (escape-html "\"") "&quot;"))
  (is (= (escape-html "<") "&lt;"))
  (is (= (escape-html ">") "&gt;"))
  (is (= (escape-html "&") "&amp;"))
  (is (= (escape-html "foo") "foo"))
  (is (= (escape-html "'") "&apos;"))
  (is (= (binding [*html-mode* :sgml] (escape-html "'")) "&#39;")))

(deftest test-as-str
  (is (= (as-str "foo") "foo"))
  (is (= (as-str :foo) "foo"))
  (is (= (as-str 100) "100"))
  #?(:clj (is (= (as-str 4/3) (str (float 4/3)))))
  (is (= (as-str "a" :b 3) "ab3"))
  (is (= (as-str (make-uri "/foo")) "/foo"))
  (is (= (as-str (make-uri "localhost:3000/foo")) "localhost:3000/foo")))

(deftest test-to-uri
  (testing "with no base URL"
    (is (= (to-str (to-uri "foo")) "foo"))
    (is (= (to-str (to-uri "/foo/bar")) "/foo/bar"))
    (is (= (to-str (to-uri "/foo#bar")) "/foo#bar")))
  (testing "with base URL"
    (with-base-url "/foo"
      (is (= (to-str (to-uri "/bar")) "/foo/bar"))
      (is (= (to-str (to-uri "http://example.com")) "http://example.com"))
      (is (= (to-str (to-uri "https://example.com/bar")) "https://example.com/bar"))
      (is (= (to-str (to-uri "bar")) "bar"))
      (is (= (to-str (to-uri "../bar")) "../bar"))
      (is (= (to-str (to-uri "//example.com/bar")) "//example.com/bar"))))
  (testing "with base URL for root context"
    (with-base-url "/"
      (is (= (to-str (to-uri "/bar")) "/bar"))
      (is (= (to-str (to-uri "http://example.com")) "http://example.com"))
      (is (= (to-str (to-uri "https://example.com/bar")) "https://example.com/bar"))
      (is (= (to-str (to-uri "bar")) "bar"))
      (is (= (to-str (to-uri "../bar")) "../bar"))
      (is (= (to-str (to-uri "//example.com/bar")) "//example.com/bar"))))
  (testing "with base URL containing trailing slash"
    (with-base-url "/foo/"
      (is (= (to-str (to-uri "/bar")) "/foo/bar"))
      (is (= (to-str (to-uri "http://example.com")) "http://example.com"))
      (is (= (to-str (to-uri "https://example.com/bar")) "https://example.com/bar"))
      (is (= (to-str (to-uri "bar")) "bar"))
      (is (= (to-str (to-uri "../bar")) "../bar"))
      (is (= (to-str (to-uri "//example.com/bar")) "//example.com/bar")))))

(deftest test-url-encode
  (testing "strings"
    (are [s e] (= (url-encode s) e)
      "a"   "a"
      "a b" "a+b"
      "&"   "%26"))
  (testing "parameter maps"
    (are [m e] (= (url-encode m) e)
      {"a" "b"}       "a=b"
      {:a "b"}        "a=b"
      {:a "b" :c "d"} "a=b&c=d"
      {:a "&"}        "a=%26"
      {:é "è"}        "%C3%A9=%C3%A8"))
  (testing "different encodings"
    #?@(:clj [(are [e s]
                  (= (with-encoding e (url-encode {:iroha "いろは"})) s)
                "UTF-8"       "iroha=%E3%81%84%E3%82%8D%E3%81%AF"
                "Shift_JIS"   "iroha=%82%A2%82%EB%82%CD"
                "EUC-JP"      "iroha=%A4%A4%A4%ED%A4%CF"
                "ISO-2022-JP" "iroha=%1B%24%42%24%24%24%6D%24%4F%1B%28%42")]
             :cljs [(are [e s]
                        (thrown?
                         js/Error
                         (with-encoding e (url-encode {:iroha "いろは"})) s)
                      "Shift_JIS"   "iroha=%82%A2%82%EB%82%CD"
                      "EUC-JP"      "iroha=%A4%A4%A4%ED%A4%CF"
                      "ISO-2022-JP" "iroha=%1B%24%42%24%24%24%6D%24%4F%1B%28%42")
                    (is (= (with-encoding "UTF-8"
                             (url-encode {:iroha "いろは"}))
                           "iroha=%E3%81%84%E3%82%8D%E3%81%AF"))])))

(deftest test-url
  (testing "URL parts and parameters"
    (are [u s] (= u s)
      (url "foo")          (make-uri "foo")
      (url "foo/" 1)       (make-uri "foo/1")
      (url "/foo/" "bar")  (make-uri "/foo/bar")
      (url {:a "b"})       (make-uri "?a=b")
      (url "foo" {:a "&"}) (make-uri "foo?a=%26")
      (url "/foo/" 1 "/bar" {:page 2})
      (make-uri "/foo/1/bar?page=2"))))
