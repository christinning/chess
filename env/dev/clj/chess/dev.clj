(ns chess.dev
  (:require [environ.core :refer [env]]
            [net.cgrand.enlive-html :refer [set-attr prepend append html]]
            [figwheel-sidecar.auto-builder :as fig-auto]
            [figwheel-sidecar.core :as fig]))

(def is-dev? (env :is-dev))

(def inject-devmode-html
  (comp
     (set-attr :class "is-dev")
     (prepend (html [:script {:type "text/javascript" :src "/js/out/goog/base.js"}]))
     (prepend (html [:script {:type "text/javascript" :src "/react/react.js"}]))
     (append  (html [:script {:type "text/javascript"} "goog.require('chess.dev')"]))
     ))

(defn cljs-repl []
  (figwheel-sidecar.repl-api/cljs-repl))

