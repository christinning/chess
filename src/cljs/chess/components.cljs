(ns chess.components
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chess.board :refer [move as-rows in pieces colour squares-and-pieces]]
            [cljs.core.async :refer [chan <! put!]]))


(defn piece-class-name
  [p]
  (str "piece " (colour p) " " (pieces p)))

(defn piece [p owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className (piece-class-name p)} nil))
    om/IDisplayName
    (display-name [_]
      "piece")))

(defn square [[id p selected] owner {:keys [click-chan]}]
  (reify
    om/IRender
    (render [_]
      (dom/div
       #js {:className (str "square" (if selected " selected"))
            :id        id
            :onClick   #(put! click-chan id)}
       (if p
         (om/build piece p))))
    om/IDisplayName
    (display-name [_]
      "square")))

(defn board [{:keys [board] :as app-state} owner]
  (reify
    om/IInitState
    (init-state [_]
      {:click-chan (chan)})
    om/IRenderState
    (render-state [this {click-chan :click-chan selected :selected}]
      (apply dom/div #js {:className "board"}
             (map
               (fn
                 [r]
                 (apply dom/div #js {:className "row"}
                        (om/build-all square r {:opts {:click-chan click-chan}})))
               (->> board
                    squares-and-pieces
                    (map (fn [[s p]] [s p (= s selected)]))
                    as-rows))))
    om/IDidMount
    (did-mount [_]
      (let [click-chan (om/get-state owner :click-chan)]
        (go-loop []
          (let [new-selection (<! click-chan)
                old-selection (om/get-state owner :selected)]
            (if old-selection
              (do
                (om/transact! app-state :board (fn [b]
                                                 (move b
                                                         old-selection
                                                         new-selection)))
                (om/set-state! owner :selected nil))
              (if (in (deref board) new-selection)
                (om/set-state! owner :selected new-selection))))
          (recur))))
    om/IDisplayName
    (display-name [_]
      "board")))
