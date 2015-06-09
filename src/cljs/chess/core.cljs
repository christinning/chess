(ns ^:figwheel-always chess.core
  (:require [om.core :as om :include-macros true]
            [chess.board :as board]
            [chess.components :as components]))

(defonce app-state (atom {:board board/start-position}))

(defn main []
  (om/root
   components/board
   app-state
   {:target (. js/document (getElementById "app"))}))

