(ns chess.core
  (:require [om.core :as om :include-macros true]
            [chess.board :refer [start-position]]
            [chess.components :as components]))

(defonce app-state (atom {:board start-position}))

(defn main []
  (om/root
   components/board
   app-state
   {:target (. js/document (getElementById "app"))}))

