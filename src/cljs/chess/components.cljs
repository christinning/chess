(ns chess.components
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [chess.board :refer [move as-rows in pieces colour squares-and-pieces]]
            [cljs.core.async :refer [chan <! put!]]
            [clojure.string :refer [join]]))


(defn piece-class-name
  [p]
  (join " " ["piece" (-> p colour name) (pieces p)]))

(defn piece [p owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className (piece-class-name p)} nil))
    om/IDisplayName
    (display-name [_]
      "piece")))

(defn square [[id p selected] owner {:keys [action-chan]}]
  (reify
    om/IRender
    (render [_]
      (dom/div
       #js {:className (str "square" (if selected " selected"))
            :id        id
            :onClick   #(put! action-chan {:type :click :square id})}
       (if p
         (om/build piece p))))
    om/IDisplayName
    (display-name [_]
      "square")))



(defn board [{:keys [game hist] :as app-state} owner]
  (reify
    om/IInitState
    (init-state [_]
      {:action-chan (chan)})
    om/IRenderState
    (render-state [this {action-chan :action-chan selected :selected}]
      (apply dom/div #js {:className "board"}
             (map
               (fn
                 [r]
                 (apply dom/div #js {:className "row"}
                        (om/build-all square r {:opts {:action-chan action-chan}})))
               (->> game
                    squares-and-pieces
                    (map (fn [[s p]] [s p (= s selected)]))
                    as-rows))))
    om/IDidMount
    (did-mount [_]
      (let [action-chan (om/get-state owner :action-chan)]
        (.addEventListener js/window "keyup" #(put! action-chan {:type :keyup
                                                                 :key (.-keyCode %)}))
        (go-loop []
                 (let [message (<! action-chan)
                       message-type (message :type)]
                   (condp = message-type
                     :click (let [new-selection (message :square)
                                  old-selection (om/get-state owner :selected)]
                              (if old-selection
                                (do
                                  (om/transact! app-state
                                                (fn [{:keys [game hist]}]
                                                  {:game (move game old-selection new-selection)
                                                   :hist (vec (cons game hist))}))
                                  (om/set-state! owner :selected nil))
                                (if (in (deref game) new-selection)
                                  (om/set-state! owner :selected new-selection))))
                     :keyup (if (= 90 (message :key))
                              (do
                                (om/transact! app-state
                                              (fn [{hist :hist :as state}]
                                                (if-not (empty? hist)
                                                  {:game (first hist)
                                                   :hist (rest hist)}
                                                  state)))

                                (om/set-state! owner :selected nil)))))
                 (recur))))
    om/IDisplayName
    (display-name [_]
      "board")))
