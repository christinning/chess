(ns chess.dev
  (:require [environ.core :refer [env]]
            [net.cgrand.enlive-html :refer [set-attr prepend append html]]
            [figwheel-sidecar.auto-builder :as fig-auto]
            [figwheel-sidecar.core :as fig]))

(def is-dev? (env :is-dev))

(def inject-devmode-html
  (comp
     (set-attr :class "is-dev")
     (prepend (html [:script {:type "text/javascript" :src "/js/goog/base.js"}]))
     (prepend (html [:script {:type "text/javascript" :src "/react/react.js"}]))
     ;;(append  (html [:script {:type "text/javascript"} "goog.require('chess.dev')"]))
     ))


(defn start-figwheel []
  (let [server (fig/start-server { :css-dirs ["resources/public/css"]})
        config {:builds [{:id "dev"
                          :source-paths ["env/dev/cljs" "src/cljs"]
                          :compiler {:output-to            "resources/public/js/app.js"
                                     :output-dir           "resources/public/js/out"
                                     :source-map           "resources/public/js/out.js.map"
                                     :source-map-timestamp true
                                     :preamble             ["react/react.min.js"]}}]
                :figwheel-server server}]
    (fig-auto/autobuild* config)))
