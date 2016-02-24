(ns hiccup.core
  "Library for rendering a tree of vectors into a string of HTML.
  Pre-compiles where possible for performance when called from clojure."
  (:use hiccup.compiler-macros
        hiccup.compiler
        hiccup.util))

(defn wrap-no-escape-strings [compile-fn content]
  `(if-not *no-escape-strings*
     (binding [*no-escape-strings* identity-set]
       (str ~(apply compile-fn content)))
     (let [out-str# ~(apply compile-fn content)]
       (set! *no-escape-strings*
             (conj *no-escape-strings* out-str#))
       out-str#)))

(defmacro html
  "Render Clojure data structures to a string of HTML."
  [options & content]
  (let [mode (and (map? options) (:mode options))
        content (if mode content (cons options content))
        cljs-env? (cljs-env? &env)]
    (cond (and cljs-env? mode)
          `(binding [*html-mode* (or ~mode *html-mode*)]
            ~(wrap-no-escape-strings compile-html* content))
          cljs-env?
          (wrap-no-escape-strings compile-html* content)
          mode
          (binding [*html-mode* (or mode *html-mode*)]
            `(binding [*html-mode* (or ~mode *html-mode*)]
               ~(wrap-no-escape-strings compile-html content)))
          :else
          (wrap-no-escape-strings compile-html content))))

(def ^{:doc "Alias for hiccup.util/escape-html"}
  h escape-html)
