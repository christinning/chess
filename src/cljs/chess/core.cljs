(ns chess.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chess.board :as board]
            [chess.components :refer [square]]
            [cljs.core.async :refer [chan <! put!]]))



(defonce app-state (atom {:board board/start-position}))


(let [click-chan (chan)]
  (defn main []
    (om/root
     (fn [{:keys [board selected] :as app-state} owner]
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
                          board/squares-and-pieces
                          (map (fn [[s p]] [s p (= s selected)]))
                          board/in-rows)))))
         om/IDidMount
         (did-mount [_]
           (go (while true
                 (let [new-selection (<! click-chan)
                       old-selection (om/get-state owner :selected)]
                   (if old-selection
                     (do
                       (om/transact! app-state :board (fn [b] (board/move b old-selection new-selection)))
                       (om/set-state! owner :selected nil))
                     (if (board/in (deref board) new-selection)
                       (om/set-state! owner :selected new-selection)))))))))
     app-state
     {:target (. js/document (getElementById "app"))})))

