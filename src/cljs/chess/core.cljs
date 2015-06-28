(ns chess.core
  (:require [om.core :as om :include-macros true]
            [chess.board :refer [new-game]]
            [chess.components :as components]))

(defonce app-state (atom {:game new-game}))

(defn main []
  (om/root
   components/board
   app-state
   {:target (. js/document (getElementById "app"))}))

