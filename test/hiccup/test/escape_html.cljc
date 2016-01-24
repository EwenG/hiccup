(ns hiccup.test.escape-html
  (:require [hiccup.core
               #?(:clj :refer :cljs :refer-macros) [html]]
            [hiccup.def #?(:clj :refer :cljs :refer-macros) [defhtml]]
            [hiccup.util #?(:clj :refer :cljs :refer-macros)
             [deftest is testing run-tests run-all-tests are]]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros
                      [deftest is testing run-tests
                       run-all-tests are]])))

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
    (def compiled-static-string-1 (html [:div "<p></p>"]))
    (is (= (html compiled-static-string-1)
           "&lt;div&gt;&amp;lt;p&amp;gt;&amp;lt;/p&amp;gt;&lt;/div&gt;")))
  (testing "wrapping compiled string results in too much escaping"
    (is (= (html (str (compiled-string) "<img/>"))
           "&lt;div&gt;&amp;lt;p&amp;gt;&amp;lt;/p&amp;gt;&lt;/div&gt;&lt;img/&gt;"))))

(deftest avoid-escaping
  (testing "without-escape-html avoids string escaping"
    (def compiled-static-string-2 (html [:div "<p></p>"]))
    (is (= (html (hiccup.util/without-escape-html compiled-static-string-2))
           "<div>&lt;p&gt;&lt;/p&gt;</div>"))))
