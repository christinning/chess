(defproject fchess "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}

            :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                           [org.clojure/clojurescript "0.0-3211"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                           [ring "1.3.1"]
                           [compojure "1.3.4"]
                           [enlive "1.1.5"]
                           [om "0.7.3"]
                           [figwheel "0.3.3"]
                           [environ "1.0.0"]]

            :plugins [[lein-cljsbuild "1.0.5"]
                      [lein-figwheel "0.3.3"]
                      [lein-environ "1.0.0"]]

            :source-paths ["src" "src/cljs"]

            :clean-targets ^{:protect false} ["resources/public/js/" "target"]

            :cljsbuild {
                        :builds [{:id "dev"
                                  :source-paths ["src/cljs"]

                                  :figwheel { :on-jsload "chess.core/on-js-reload" }

                                  :compiler {:main chess.core
                                             :asset-path "js/"
                                             :output-to "resources/public/js/app.js"
                                             :output-dir "resources/public/js/"
                                             :source-map-timestamp true }}
                                 {:id "min"
                                  :source-paths ["src/cljs"]
                                  :compiler {:output-to "resources/public/js/app.js"
                                             :main chess.core
                                             :optimizations :advanced
                                             :pretty-print false}}]}

            :figwheel {
                       :http-server-root "" ;; default and assumes "resources"
                       :server-port 10555 ;; default
                       :css-dirs ["resources/public/css"] ;; watch and update CSS

                       ;; Start an nREPL server into the running figwheel process
                       :nrepl-port 7889

                       ;; Server Ring Handler (optional)
                       ;; if you want to embed a ring handler into the figwheel http-kit
                       ;; server, this is for simple ring servers, if this
                       ;; doesn't work for you just run your own server :)
                       ;; :ring-handler hello_world.server/handler

                       ;; To be able to open files in your editor from the heads up display
                       ;; you will need to put a script on your path.
                       ;; that script will have to take a file path and a line number
                       ;; ie. in  ~/bin/myfile-opener
                       ;; #! /bin/sh
                       ;; emacsclient -n +$2 $1
                       ;;
                       ;; :open-file-command "myfile-opener"

                       ;; if you want to disable the REPL
                       ;; :repl false

                       ;; to configure a different figwheel logfile path
                       ;; :server-logfile "tmp/logs/figwheel-logfile.log"
                       })
