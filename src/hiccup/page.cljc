(ns hiccup.page
  "Functions for setting up HTML pages."
  (:require [hiccup.util :refer [raw-string to-uri]]
            #?(:clj [hiccup.core :refer [html]])
            #?(:clj [hiccup.def :refer [defelem]]))
  #?(:cljs (:require-macros [hiccup.core :refer [html]]
                            [hiccup.def :refer [defelem]])))

(def doctype
  {:html4
   (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" "
        "\"http://www.w3.org/TR/html4/strict.dtd\">\n")
   :xhtml-strict
   (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
        "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n")
   :xhtml-transitional
   (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
        "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n")
   :html5
   "<!DOCTYPE html>\n"})

(defelem xhtml-tag
  "Create an XHTML element for the specified language."
  [lang & contents]
  [:html {:xmlns "http://www.w3.org/1999/xhtml"
          "xml:lang" lang
          :lang lang}
    contents])

(defn xml-declaration
  "Create a standard XML declaration for the following encoding."
  [encoding]
  (raw-string "<?xml version=\"1.0\" encoding=\"" encoding "\"?>\n"))

(defn include-js
  "Include a list of external javascript files."
  [& scripts]
  (for [script scripts]
    [:script {:type "text/javascript", :src (to-uri script)}]))

(defn include-css
  "Include a list of external stylesheet files."
  [& styles]
  (for [style styles]
    [:link {:type "text/css", :href (to-uri style), :rel "stylesheet"}]))

#?(:clj
   (defmacro html4
     "Create a HTML 4 document with the supplied contents. The first
  argument may be an optional attribute map."
     [& contents]
     `(html {:mode :sgml}
            (raw-string (doctype :html4))
            [:html ~@contents])))

#?(:clj
   (defmacro xhtml
     "Create a XHTML 1.0 strict document with the supplied contents.
  The first argument may be an optional attribute may. The following
  attributes are treated specially:
    :lang     - The language of the document
    :encoding - The character encoding of the document, defaults to UTF-8."
     [options & contents]
     (if-not (map? options)
       `(xhtml {} ~options ~@contents)
       `(let [options# ~options]
          (html {:mode :xml}
                (xml-declaration (options# :encoding "UTF-8"))
                (raw-string (doctype :xhtml-strict))
                (xhtml-tag (options# :lang) ~@contents))))))

#?(:clj
   (defmacro html5
     "Create a HTML5 document with the supplied contents."
     [options & contents]
     (if-not (map? options)
       `(html5 {} ~options ~@contents)
       (if (options :xml?)
         `(let [options# (dissoc ~options :xml?)]
            (html {:mode :xml}
                  (xml-declaration (options# :encoding "UTF-8"))
                  (raw-string (doctype :html5))
                  (xhtml-tag options# (options# :lang) ~@contents)))
         `(let [options# (dissoc ~options :xml?)]
            (html {:mode :html}
                  (raw-string (doctype :html5))
                  [:html options# ~@contents]))))))
