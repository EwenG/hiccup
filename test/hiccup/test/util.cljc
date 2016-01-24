(ns hiccup.test.util
  (:require [hiccup.core #?(:clj :refer :cljs :refer-macros) [html]]
            [hiccup.util :as util #?(:clj :refer :cljs :refer-macros)
             [with-base-url with-encoding]]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros
                      [deftest is testing run-tests run-all-tests are]]))
  (:import #?(:clj [java.net URI] :cljs [goog Uri])))

(defn make-uri [s]
  #?(:clj (URI. s)
     :cljs (Uri.parse s)))

(deftest test-escaped-chars
  (is (= (util/escape-html "\"") "&quot;"))
  (is (= (util/escape-html "<") "&lt;"))
  (is (= (util/escape-html ">") "&gt;"))
  (is (= (util/escape-html "&") "&amp;"))
  (is (= (util/escape-html "foo") "foo"))
  (is (= (util/escape-html "'") "&apos;"))
  (is (= (binding [util/*html-mode* :sgml] (util/escape-html "'")) "&#39;")))

(deftest test-as-str
  (is (= (util/as-str "foo") "foo"))
  (is (= (util/as-str :foo) "foo"))
  (is (= (util/as-str 100) "100"))
  #?(:clj (is (= (util/as-str 4/3) (str (float 4/3)))))
  (is (= (util/as-str "a" :b 3) "ab3"))
  (is (= (util/as-str (make-uri "/foo")) "/foo"))
  (is (= (util/as-str (make-uri "localhost:3000/foo")) "localhost:3000/foo")))

(deftest test-to-uri
  (testing "with no base URL"
    (is (= (util/to-str (util/to-uri "foo")) "foo"))
    (is (= (util/to-str (util/to-uri "/foo/bar")) "/foo/bar"))
    (is (= (util/to-str (util/to-uri "/foo#bar")) "/foo#bar")))
  (testing "with base URL"
    (with-base-url "/foo"
      (is (= (util/to-str (util/to-uri "/bar")) "/foo/bar"))
      (is (= (util/to-str (util/to-uri "http://example.com")) "http://example.com"))
      (is (= (util/to-str (util/to-uri "https://example.com/bar")) "https://example.com/bar"))
      (is (= (util/to-str (util/to-uri "bar")) "bar"))
      (is (= (util/to-str (util/to-uri "../bar")) "../bar"))
      (is (= (util/to-str (util/to-uri "//example.com/bar")) "//example.com/bar"))))
  (testing "with base URL for root context"
    (with-base-url "/"
      (is (= (util/to-str (util/to-uri "/bar")) "/bar"))
      (is (= (util/to-str (util/to-uri "http://example.com")) "http://example.com"))
      (is (= (util/to-str (util/to-uri "https://example.com/bar")) "https://example.com/bar"))
      (is (= (util/to-str (util/to-uri "bar")) "bar"))
      (is (= (util/to-str (util/to-uri "../bar")) "../bar"))
      (is (= (util/to-str (util/to-uri "//example.com/bar")) "//example.com/bar"))))
  (testing "with base URL containing trailing slash"
    (with-base-url "/foo/"
      (is (= (util/to-str (util/to-uri "/bar")) "/foo/bar"))
      (is (= (util/to-str (util/to-uri "http://example.com")) "http://example.com"))
      (is (= (util/to-str (util/to-uri "https://example.com/bar")) "https://example.com/bar"))
      (is (= (util/to-str (util/to-uri "bar")) "bar"))
      (is (= (util/to-str (util/to-uri "../bar")) "../bar"))
      (is (= (util/to-str (util/to-uri "//example.com/bar")) "//example.com/bar")))))

(deftest test-url-encode
  (testing "strings"
    (are [s e] (= (util/url-encode s) e)
      "a"   "a"
      "a b" "a+b"
      "&"   "%26"))
  (testing "parameter maps"
    (are [m e] (= (util/url-encode m) e)
      {"a" "b"}       "a=b"
      {:a "b"}        "a=b"
      {:a "b" :c "d"} "a=b&c=d"
      {:a "&"}        "a=%26"
      {:é "è"}        "%C3%A9=%C3%A8"))
  (testing "different encodings"
    #?@(:clj [(are [e s]
                  (= (with-encoding e (util/url-encode {:iroha "いろは"})) s)
                "UTF-8"       "iroha=%E3%81%84%E3%82%8D%E3%81%AF"
                "Shift_JIS"   "iroha=%82%A2%82%EB%82%CD"
                "EUC-JP"      "iroha=%A4%A4%A4%ED%A4%CF"
                "ISO-2022-JP" "iroha=%1B%24%42%24%24%24%6D%24%4F%1B%28%42")]
             :cljs [(are [e s]
                        (thrown?
                         js/Error
                         (with-encoding e (util/url-encode {:iroha "いろは"})) s)
                      "Shift_JIS"   "iroha=%82%A2%82%EB%82%CD"
                      "EUC-JP"      "iroha=%A4%A4%A4%ED%A4%CF"
                      "ISO-2022-JP" "iroha=%1B%24%42%24%24%24%6D%24%4F%1B%28%42")
                    (is (= (with-encoding "UTF-8"
                             (util/url-encode {:iroha "いろは"}))
                           "iroha=%E3%81%84%E3%82%8D%E3%81%AF"))])))

(deftest test-url
  (testing "URL parts and parameters"
    (are [u s] (= u s)
      (util/url "foo")          (make-uri "foo")
      (util/url "foo/" 1)       (make-uri "foo/1")
      (util/url "/foo/" "bar")  (make-uri "/foo/bar")
      (util/url {:a "b"})       (make-uri "?a=b")
      (util/url "foo" {:a "&"}) (make-uri "foo?a=%26")
      (util/url "/foo/" 1 "/bar" {:page 2})
      (make-uri "/foo/1/bar?page=2"))))
