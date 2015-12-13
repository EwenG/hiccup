(ns hiccup.page-macros
  "Functions for setting up HTML pages."
  (:require [hiccup.core :refer [html]]
            [hiccup.util :refer [without-escape-html]]
            [hiccup.page :refer [xml-declaration xhtml-tag doctype]]))

(defmacro html4
  "Create a HTML 4 document with the supplied contents. The first argument
  may be an optional attribute map."
  [& contents]
  `(html {:mode :sgml}
         (without-escape-html (doctype :html4))
     [:html ~@contents]))

(defmacro xhtml
  "Create a XHTML 1.0 strict document with the supplied contents. The first
  argument may be an optional attribute may. The following attributes are
  treated specially:
    :lang     - The language of the document
    :encoding - The character encoding of the document, defaults to UTF-8."
  [options & contents]
  (if-not (map? options)
    `(xhtml {} ~options ~@contents)
    `(let [options# ~options]
       (html {:mode :xml}
         (xml-declaration (options# :encoding "UTF-8"))
         (without-escape-html (doctype :xhtml-strict))
         (xhtml-tag (options# :lang) ~@contents)))))

(defmacro html5
  "Create a HTML5 document with the supplied contents."
  [options & contents]
  (if-not (map? options)
    `(html5 {} ~options ~@contents)
    (if (options :xml?)
      `(let [options# (dissoc ~options :xml?)]
         (html {:mode :xml}
           (xml-declaration (options# :encoding "UTF-8"))
           (without-escape-html (doctype :html5))
           (xhtml-tag options# (options# :lang) ~@contents)))
      `(let [options# (dissoc ~options :xml?)]
         (html {:mode :html}
               (without-escape-html (doctype :html5))
           [:html options# ~@contents])))))
