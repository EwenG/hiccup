Hiccup
======

A safe version of the original Hiccup library.
All strings are automatically escaped.

```clojure
user=> (html [:span "<img/>"])
"<span>&lt;img/&gt;</span>"
```

```clojure
user=> (html [:span (without-escape-html "<img/>")])
"<span><img/></span>"
```
