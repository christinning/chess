(ns chess.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chess.board :as board]
            [cljs.core.async :refer [chan <! put!]]))



(defonce app-state (atom {:board board/start-position :selected "a1"}))


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

(let [click-chan (chan)]
  (defn main []
    (om/root
     (fn [{:keys [board selected] :as app-state} owner]
       (reify
         om/IRender
         (render [_]
           (apply dom/div #js {:className "board"}
                  (map
                   (fn
                     [r]
                     (apply dom/div #js {:className "row"}
                            (om/build-all square r {:opts {:click-chan click-chan}})))
                   (->> board
                        board/squares-and-pieces
                        (map (fn [[s p]] [s p (= s selected)]))
                        board/in-rows))))
         om/IDidMount
         (did-mount [_]
           (go (while true
                 (let [new-selection (<! click-chan)]
                   (om/transact! app-state :selected (fn [_] new-selection))))))))
     app-state
     {:target (. js/document (getElementById "app"))})))

