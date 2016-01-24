(ns hiccup.test.form
  (:require [hiccup.core #?(:clj :refer :cljs :refer-macros) [html]]
            [hiccup.form :as hform #?@(:cljs [:refer-macros [with-group]])]
            #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros
                      [deftest is testing run-tests run-all-tests are]])))

(deftest test-hidden-field
  (is (= (html (hform/hidden-field :foo "bar"))
         "<input id=\"foo\" name=\"foo\" type=\"hidden\" value=\"bar\" />")))

(deftest test-hidden-field-with-extra-atts
  (is (= (html (hform/hidden-field {:class "classy"} :foo "bar"))
         "<input class=\"classy\" id=\"foo\" name=\"foo\" type=\"hidden\" value=\"bar\" />")))

(deftest test-text-field
  (is (= (html (hform/text-field :foo))
         "<input id=\"foo\" name=\"foo\" type=\"text\" />")))

(deftest test-text-field-with-extra-atts
  (is (= (html (hform/text-field {:class "classy"} :foo "bar"))
         "<input class=\"classy\" id=\"foo\" name=\"foo\" type=\"text\" value=\"bar\" />")))

(deftest test-check-box
  (is (= (html (hform/check-box :foo true))
         (str "<input checked=\"checked\" id=\"foo\" name=\"foo\""
              " type=\"checkbox\" value=\"true\" />"))))

(deftest test-check-box-with-extra-atts
  (is (= (html (hform/check-box {:class "classy"} :foo true 1))
         (str "<input checked=\"checked\" class=\"classy\" id=\"foo\" name=\"foo\""
              " type=\"checkbox\" value=\"1\" />"))))

(deftest test-password-field
  (is (= (html (hform/password-field :foo "bar"))
         "<input id=\"foo\" name=\"foo\" type=\"password\" value=\"bar\" />")))

(deftest test-password-field-with-extra-atts
  (is (= (html (hform/password-field {:class "classy"} :foo "bar"))
         "<input class=\"classy\" id=\"foo\" name=\"foo\" type=\"password\" value=\"bar\" />")))

(deftest test-email-field
  (is (= (html (hform/email-field :foo "bar"))
         "<input id=\"foo\" name=\"foo\" type=\"email\" value=\"bar\" />")))

(deftest test-email-field-with-extra-atts
  (is (= (html (hform/email-field {:class "classy"} :foo "bar"))
         "<input class=\"classy\" id=\"foo\" name=\"foo\" type=\"email\" value=\"bar\" />")))

(deftest test-radio-button
  (is (= (html (hform/radio-button :foo true 1))
         (str "<input checked=\"checked\" id=\"foo-1\" name=\"foo\""
              " type=\"radio\" value=\"1\" />"))))

(deftest test-radio-button-with-extra-atts
  (is (= (html (hform/radio-button {:class "classy"} :foo true 1))
         (str "<input checked=\"checked\" class=\"classy\" id=\"foo-1\" name=\"foo\""
              " type=\"radio\" value=\"1\" />"))))

(deftest test-select-options
  (are [x y] (= (html x) y)
    (hform/select-options ["foo" "bar" "baz"])
      "<option>foo</option><option>bar</option><option>baz</option>"
      (hform/select-options ["foo" "bar"] "bar")
      "<option>foo</option><option selected=\"selected\">bar</option>"
      (hform/select-options [["Foo" 1] ["Bar" 2]])
      "<option value=\"1\">Foo</option><option value=\"2\">Bar</option>"
      (hform/select-options [["Foo" [1 2]] ["Bar" [3 4]]])
      (str "<optgroup label=\"Foo\"><option>1</option><option>2</option></optgroup>"
           "<optgroup label=\"Bar\"><option>3</option><option>4</option></optgroup>")
      (hform/select-options [["Foo" [["bar" 1] ["baz" 2]]]])
      (str "<optgroup label=\"Foo\"><option value=\"1\">bar</option>"
           "<option value=\"2\">baz</option></optgroup>")
      (hform/select-options [["Foo" [1 2]]] 2)
      (str "<optgroup label=\"Foo\"><option>1</option>"
           "<option selected=\"selected\">2</option></optgroup>")))



(deftest test-drop-down
  (let [options ["op1" "op2"]
        selected "op1"
        select-options (html (hform/select-options options selected))]
    (is (= (html (hform/drop-down :foo options selected))
           (str "<select id=\"foo\" name=\"foo\">" select-options "</select>")))))

(deftest test-drop-down-with-extra-atts
  (let [options ["op1" "op2"]
        selected "op1"
        select-options (html (hform/select-options options selected))]
    (is (= (html (hform/drop-down {:class "classy"} :foo options selected))
           (str "<select class=\"classy\" id=\"foo\" name=\"foo\">"
                select-options "</select>")))))

(deftest test-text-area
  (is (= (html (hform/text-area :foo "bar"))
         "<textarea id=\"foo\" name=\"foo\">bar</textarea>")))

(deftest test-text-area-field-with-extra-atts
  (is (= (html (hform/text-area {:class "classy"} :foo "bar"))
         "<textarea class=\"classy\" id=\"foo\" name=\"foo\">bar</textarea>")))

(deftest test-text-area-escapes
  (is (= (html (hform/text-area :foo "bar</textarea>"))
         "<textarea id=\"foo\" name=\"foo\">bar&lt;/textarea&gt;</textarea>")))

(deftest test-file-field
  (is (= (html (hform/file-upload :foo))
         "<input id=\"foo\" name=\"foo\" type=\"file\" />")))

(deftest test-file-field-with-extra-atts
  (is (= (html (hform/file-upload {:class "classy"} :foo))
         (str "<input class=\"classy\" id=\"foo\" name=\"foo\""
              " type=\"file\" />"))))

(deftest test-label
  (is (= (html (hform/label :foo "bar"))
         "<label for=\"foo\">bar</label>")))

(deftest test-label-with-extra-atts
  (is (= (html (hform/label {:class "classy"} :foo "bar"))
         "<label class=\"classy\" for=\"foo\">bar</label>")))

(deftest test-submit
  (is (= (html (hform/submit-button "bar"))
         "<input type=\"submit\" value=\"bar\" />")))

(deftest test-submit-button-with-extra-atts
  (is (= (html (hform/submit-button {:class "classy"} "bar"))
         "<input class=\"classy\" type=\"submit\" value=\"bar\" />")))

(deftest test-reset-button
  (is (= (html (hform/reset-button "bar"))
         "<input type=\"reset\" value=\"bar\" />")))

(deftest test-reset-button-with-extra-atts
  (is (= (html (hform/reset-button {:class "classy"} "bar"))
         "<input class=\"classy\" type=\"reset\" value=\"bar\" />")))

(deftest test-form-to
  (is (= (html (hform/form-to [:post "/path"] "foo" "bar"))
         "<form action=\"/path\" method=\"POST\">foobar</form>")))

(deftest test-form-to-with-hidden-method
  (is (= (html (hform/form-to [:put "/path"] "foo" "bar"))
         (str "<form action=\"/path\" method=\"POST\">"
              "<input id=\"_method\" name=\"_method\" type=\"hidden\" value=\"PUT\" />"
              "foobar</form>"))))

(deftest test-form-to-with-extr-atts
  (is (= (html (hform/form-to {:class "classy"} [:post "/path"] "foo" "bar"))
         "<form action=\"/path\" class=\"classy\" method=\"POST\">foobar</form>")))

(deftest test-with-group
  (testing "hidden-field"
    (is (= (html (hform/with-group :foo (hform/hidden-field :bar "val")))
           "<input id=\"foo-bar\" name=\"foo[bar]\" type=\"hidden\" value=\"val\" />")))
  (testing "text-field"
    (is (= (html (hform/with-group :foo (hform/text-field :bar)))
           "<input id=\"foo-bar\" name=\"foo[bar]\" type=\"text\" />")))
  (testing "checkbox"
    (is (= (html (hform/with-group :foo (hform/check-box :bar)))
           "<input id=\"foo-bar\" name=\"foo[bar]\" type=\"checkbox\" value=\"true\" />")))
  (testing "password-field"
    (is (= (html (hform/with-group :foo (hform/password-field :bar)))
           "<input id=\"foo-bar\" name=\"foo[bar]\" type=\"password\" />")))
  (testing "radio-button"
    (is (= (html (hform/with-group :foo (hform/radio-button :bar false "val")))
           "<input id=\"foo-bar-val\" name=\"foo[bar]\" type=\"radio\" value=\"val\" />")))
  (testing "drop-down"
    (is (= (html (hform/with-group :foo (hform/drop-down :bar [])))
           (str "<select id=\"foo-bar\" name=\"foo[bar]\"></select>"))))
  (testing "text-area"
    (is (= (html (hform/with-group :foo (hform/text-area :bar)))
           (str "<textarea id=\"foo-bar\" name=\"foo[bar]\"></textarea>"))))
  (testing "file-upload"
    (is (= (html (hform/with-group :foo (hform/file-upload :bar)))
           "<input id=\"foo-bar\" name=\"foo[bar]\" type=\"file\" />")))
  (testing "label"
    (is (= (html (hform/with-group :foo (hform/label :bar "Bar")))
           "<label for=\"foo-bar\">Bar</label>")))
  (testing "multiple with-groups"
    (is (= (html (hform/with-group :foo (hform/with-group :bar (hform/text-field :baz))))
           "<input id=\"foo-bar-baz\" name=\"foo[bar][baz]\" type=\"text\" />")))
  (testing "multiple elements"
    (is (= (html (hform/with-group :foo (hform/label :bar "Bar") (hform/text-field :var)))
           "<label for=\"foo-bar\">Bar</label><input id=\"foo-var\" name=\"foo[var]\" type=\"text\" />"))))
