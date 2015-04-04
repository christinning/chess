(ns chess.components
  (require [om.core :as om :include-macros true]
           [om.dom :as dom :include-macros true]
           [chess.board :as board]
           [cljs.core.async :refer [put!]]))


(defn piece-class-name
  [p]
  (str "piece " (board/colour p) " " (board/pieces p)))

(defn piece [p owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className (piece-class-name p)} nil))))

(defn square [[id p selected] owner {:keys [click-chan]}]
  (reify
    om/IRender
    (render [_]
      (dom/div
       #js {:className (str "square" (if selected " selected"))
            :id id
            :onClick #(put! click-chan id)}
       (if p
         (om/build piece p))))))
