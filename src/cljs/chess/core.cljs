(ns chess.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chess.board :as board]))



(defonce app-state (atom {:board board/start-position}))


(defn piece-class-name
  [p]
  (str "piece " (board/colour p) " " (board/pieces p)))

(defn piece [p owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className (piece-class-name p)} nil))))

(defn square [[id p] owner {:keys [on-click]}]
  (reify
    om/IRender
    (render [_]
      (dom/div
       #js {:className "square" :id id}
       (if p
         (om/build piece p))))))

(defn main []
  (om/root
    (fn [{:keys [board]} owner]
      (reify
        om/IRender
        (render [_]
          (apply dom/div #js {:className "board"}
                 (map
                  (fn
                    [r]
                    (apply dom/div #js {:className "row"}
                           (om/build-all square r)))
                  (board/rows-of-squares-and-pieces board))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

