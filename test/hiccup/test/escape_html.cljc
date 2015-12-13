(ns hiccup.test.escape-html
  #?@(:clj [(:require [hiccup.core :refer [html]]
                      [hiccup.def :refer :all]
                      [clojure.test :refer :all]
                      [hiccup.util :refer :all])]
           :cljs [(:require [cljs.test :refer-macros
                             [deftest is testing run-tests
                              run-all-tests are]]
                            [hiccup.util :refer [escape-html
                                                 without-escape-html]])
                  (:require-macros [hiccup.core :refer [html]])]))

(deftest escape-strings
  (testing "strings are escaped"
    (is (= (html "<div></div>") "&lt;div&gt;&lt;/div&gt;"))
    (is (= (let [div "<div></div>"] (html div)))
        "&lt;div&gt;&lt;/div&gt;")))

(deftest escape-compiled-strings
  (testing "compiled strings are not escaped"
    (defhtml compiled-string [] [:div "<p></p>"])
    (is (= (html (compiled-string) (compiled-string) "<img/>")
           "<div>&lt;p&gt;&lt;/p&gt;</div><div>&lt;p&gt;&lt;/p&gt;</div>&lt;img/&gt;")))
  (testing "compiled strings defined outside the calling context are escaped"
    (def compiled-static-string (html [:div "<p></p>"]))
    (is (= (html compiled-static-string)
           "&lt;div&gt;&amp;lt;p&amp;gt;&amp;lt;/p&amp;gt;&lt;/div&gt;")))
  (testing "wrapping compiled string results in too much escaping"
    (is (= (html (str (compiled-string) "<img/>"))
           "&lt;div&gt;&amp;lt;p&amp;gt;&amp;lt;/p&amp;gt;&lt;/div&gt;&lt;img/&gt;"))))

(deftest avoid-escaping
  (testing "without-escape-html avoids string escaping"
    (def compiled-static-string (html [:div "<p></p>"]))
    (is (= (html (without-escape-html compiled-static-string))
           "<div>&lt;p&gt;&lt;/p&gt;</div>"))))
