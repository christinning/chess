(ns chess.components
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chess.board :as b]
            [cljs.core.async :refer [chan <! put!]]))


(defn piece-class-name
  [p]
  (str "piece " (b/colour p) " " (b/pieces p)))

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

(defn board [{:keys [board] :as app-state} owner]
  (let [click-chan (chan)]
    (reify
      om/IRenderState
      (render-state [this state]
        (let [selected (state :selected)]             
          (apply dom/div #js {:className "board"}
                 (map
                  (fn
                    [r]
                    (apply dom/div #js {:className "row"}
                           (om/build-all square r {:opts {:click-chan click-chan}})))
                  (->> board
                       b/squares-and-pieces
                       (map (fn [[s p]] [s p (= s selected)]))
                       b/as-rows)))))
      om/IDidMount
      (did-mount [_]
        (go (while true
              (let [new-selection (<! click-chan)
                    old-selection (om/get-state owner :selected)]
                (if old-selection
                  (do
                    (om/transact! app-state :board (fn [b]
                                                     (b/move b
                                                                 old-selection
                                                                 new-selection)))
                    (om/set-state! owner :selected nil))
                  (if (b/in (deref board) new-selection)
                    (om/set-state! owner :selected new-selection))))))))))
