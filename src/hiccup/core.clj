(ns hiccup.core
  "Library for rendering a tree of vectors into a string of HTML.
  Pre-compiles where possible for performance when called from clojure."
  (:use hiccup.compiler-macros
        hiccup.compiler
        hiccup.util))


(defn maybe-convert-raw-string [compile-fn content]
  `(let [out-str# (binding [*is-top-level* false]
                    ~(apply compile-fn content))]
     (if *is-top-level*
       (str out-str#) out-str#)))

(defmacro html
  "Render Clojure data structures to a string of HTML."
  [options & content]
  (let [mode (and (map? options) (:mode options))
        content (if mode content (cons options content))
        cljs-env? (cljs-env? &env)]
    (cond (and cljs-env? mode)
          `(binding [*html-mode* (or ~mode *html-mode*)]
             ~(maybe-convert-raw-string compile-html* content))
          cljs-env?
          (maybe-convert-raw-string compile-html* content)
          mode
          (binding [*html-mode* (or mode *html-mode*)]
            `(binding [*html-mode* (or ~mode *html-mode*)]
               ~(maybe-convert-raw-string compile-html content)))
          :else
          (maybe-convert-raw-string compile-html content))))

(def ^{:doc "Alias for hiccup.util/escape-html"}
  h escape-html)
