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

(defn square [s owner {:keys [rank file on-click]}]
  (reify
    om/IRender
    (render [_]
      (dom/div
       #js {:className "square" :id (board/from-board-ks file rank)}
       (if s
         (om/build piece s))))))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (apply dom/div #js {:className "board"}
                   (map-indexed
                    (fn [rank r] (apply dom/div #js {:className "row"}
                                   (map-indexed (fn [file s] (om/build square s {:opts {:rank rank :file file}})) r)))
                    (:board app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

