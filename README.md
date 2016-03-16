Hiccup
======

A Clojurescript compatible and safe (strings are automatically escaped) version of the [original Hiccup library](https://github.com/weavejester/hiccup).

Differences from Hiccup
=======================

Clojurescript support
---------------------

Following is a code example using hiccup from Clojurescript:

```clojure
(require '[hiccup.core :refer-macros [html]])
cljs.user=> (html [:div "Hello from clojurescript"])
"<div>Hello from clojurescript</div>"
```

Optional pre-compilation
------------------------

Pre-compilation of hiccup forms into string during macro expansion can be disabled. This may reduce the generated code size when compiling to javasript, at the cost of additional runtime computation.

```clojure
(require '[hiccup.core :refer-macros [html]])
cljs.user=> (html {:pre-compile false} [:div "Hello from clojurescript"])
"<div>Hello from clojurescript</div>"
```

String escaping
---------------

All strings are automatically escaped unless a string is produced by hiccup itself. Unfortunatly, there is an exception to this rule. When a string was produced outside the dynamic context of the html macro it is used from, it will always be escaped by hiccup. In this case you must instruct hiccup to avoid escaping the string by using the `hiccup.util/raw-string` function. Here are a few examples:

```clojure
user=> (require '[hiccup.core :refer [html]])
nil
user=> (require '[hiccup.def :refer [defhtml]])
nil
user=> (require '[hiccup.util :refer [raw-string]])
nil
```

```clojure
user=> (html [:span "<img/>"])
"<span>&lt;img/&gt;</span>"

user=> (html [:span (raw-string "<img/>")])
"<span><img/></span>"

user=> (defhtml foo [] [:p "A template function"])
#'hiccup.test.def/foo
user=> (html (foo))
"<p>A template function</p>"

user=> (def foo (html [:p "A static template"]))
user=> (html foo)
"&lt;p&gt;A static template&lt;/p&gt;"
user=> (html (raw-string foo))
"<p>A static template</p>"
```
