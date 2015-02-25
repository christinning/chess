(ns chess.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :refer [upper-case]]))

(def initial-board
  [[:r :n :b :q :k :b :n :r]
   [:p :p :p :p :p :p :p :p]
   [:. :. :. :. :. :. :. :.]
   [:. :. :. :. :. :. :. :.]
   [:. :. :. :. :. :. :. :.]
   [:. :. :. :. :. :. :. :.]
   [:P :P :P :P :P :P :P :P]
   [:R :N :B :Q :K :B :N :R]])

(def pieces {:r "rook" :R "rook"
             :n "knight" :N "knight"
             :b "bishop" :B "bishop"
             :q "queen" :Q "queen"
             :k "king" :K "king"
             :p "pawn" :P "pawn"})

(defn valid-piece?
  [p]
  (pieces p))

(defn white?
  [p]
  (and (valid-piece? p)
       (= (->> p str upper-case) (str p))))

(def black?
  (every-pred valid-piece? (complement white?)))

(defn colour
  [p]
  (cond (white? p) "white"
        (black? p) "black"))



(defonce app-state (atom {:board initial-board}))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (apply dom/div #js {:className "board"}
                   (map
                    (fn [r] (apply dom/div #js {:className "row"}
                                   (map (fn [s] (dom/div #js {:className "square"}
                                                         (if (valid-piece? s)
                                                           (dom/div #js {:className (str "piece " (colour s) " " (pieces s))} nil)))) r)))
                    (:board app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))
