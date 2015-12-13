(ns hiccup.compiler
  (:require [hiccup.util :refer [*html-mode* as-str escape-html
                                 maybe-escape-html]]
            [clojure.string :as str])
  #?(:clj (:import [clojure.lang IPersistentVector ISeq Named])))

(defn- xml-mode? []
  (#{:xml :xhtml} *html-mode*))

(defn- html-mode? []
  (#{:html :xhtml} *html-mode*))

(defn- end-tag []
  (if (xml-mode?) " />" ">"))

(defn- xml-attribute [name value]
  (str " " (as-str name) "=\"" (escape-html value) "\""))

(defn- render-attribute [[name value]]
  (cond
    (true? value)
      (if (xml-mode?)
        (xml-attribute name name)
        (str " " (as-str name)))
    (not value)
      ""
    :else
      (xml-attribute name value)))

(defn- render-attr-map [attrs]
  (apply str
    (sort (map render-attribute attrs))))

(def ^{:doc "Regular expression that parses a CSS-style id and class from an element name."
       :private true}
  re-tag #"([^\s\.#]+)(?:#([^\s\.#]+))?(?:\.([^\s#]+))?")

(def ^{:doc "A list of elements that must be rendered without a closing tag."
       :private true}
  void-tags
  #{"area" "base" "br" "col" "command" "embed" "hr" "img" "input" "keygen" "link"
    "meta" "param" "source" "track" "wbr"})

(defn- container-tag?
  "Returns true if the tag has content or is not a void tag. In non-HTML modes,
  all contentless tags are assumed to be void tags."
  [tag content]
  (or content
      (and (html-mode?) (not (void-tags tag)))))

(defn- merge-attributes [{:keys [id class]} map-attrs]
  (->> map-attrs
       (merge (if id {:id id}))
       (merge-with #(if %1 (str %1 " " %2) %2) (if class {:class class}))))

(defn normalize-element
  "Ensure an element vector is of the form [tag-name attrs content]."
  [[tag & content]]
  (when (not (or (keyword? tag) (symbol? tag) (string? tag)))
    #?(:clj (throw (IllegalArgumentException.
                    (str tag " is not a valid element name.")))
       :cljs (throw (js/Error. (str tag " is not a valid element name.")))))
  (let [[_ tag id class] (re-matches re-tag (as-str tag))
        tag-attrs        {:id id
                          :class (if class
                                   (str/replace ^String class "." " "))}
        map-attrs        (first content)]
    (if (map? map-attrs)
      [tag (merge-attributes tag-attrs map-attrs) (next content)]
      [tag tag-attrs content])))

(defprotocol HtmlRenderer
  (render-html [this]
    "Turn a Clojure data type into a string of HTML."))

(defn- render-element
  "Render an element vector as a HTML element."
  [element]
  (let [[tag attrs content] (normalize-element element)]
    (if (container-tag? tag content)
      (str "<" tag (render-attr-map attrs) ">"
           (render-html content)
           "</" tag ">")
      (str "<" tag (render-attr-map attrs) (end-tag)))))

(extend-protocol HtmlRenderer
  #?@(:clj [IPersistentVector
            (render-html [this]
                         (render-element this))
            ISeq
            (render-html [this]
                         (apply str (map render-html this)))
            Named
            (render-html [this]
                         (maybe-escape-html (name this)))])

  #?@(:cljs [List
             (render-html [this]
                          (apply str (map render-html this)))
             IndexedSeq
             (render-html [this]
                          (apply str (map render-html this)))
             LazySeq
             (render-html [this]
                          (apply str (map render-html this)))
             PersistentVector
             (render-html [this]
                          (render-element this))
             Keyword
             (render-html [this]
                          (maybe-escape-html (name this)))
             string
             (render-html [this] (maybe-escape-html this))
             number
             (render-html [this] (str this))])
  #?@(:clj [Object
            (render-html [this]
                         (maybe-escape-html (str this)))]
       :cljs [object
              (render-html [this]
                           (if (instance? js/String this)
                             (maybe-escape-html this)
                             (maybe-escape-html (str this))))])
  nil
  (render-html [this]
    ""))
