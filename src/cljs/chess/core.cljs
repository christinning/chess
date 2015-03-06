(ns chess.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chess.board :as board]))

(defonce app-state (atom {:board board/start-position}))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (apply dom/div #js {:className "board"}
                   (map-indexed
                    (fn [i r] (apply dom/div #js {:className "row"}
                                   (map-indexed (fn [j s] (dom/div #js {:className "square" :id (board/from-board-ks j i)}
                                                         (if s
                                                           (dom/div #js {:className (str "piece " (board/colour s) " " (board/pieces s))} nil)))) r)))
                    (:board app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))
