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

(def empty-sq :.)

(def pieces {:r "rook" :R "rook"
             :n "knight" :N "knight"
             :b "bishop" :B "bishop"
             :q "queen" :Q "queen"
             :k "king" :K "king"
             :p "pawn" :P "pawn"})

(def file->i
  "Convert a letter file to an integer index (eg \"a\" -> 0)"
  (let [m (into {}
                (map-indexed
                 (fn [i c] [c i])
                 (seq "abcdefgh")))]
    (fn [f] (get m (char f)))))

(defn parse-int
  [i]
  (js/parseInt i))

(defn to-board-ks
  "Converts a square reference in the form \"a3\" to the keys to access it in
  board structure, eg. [5 0]"
  [p]
  (let [[f r] (vec p)]
    [(- 8 (parse-int r)) (file->i f)]))

(declare valid-piece?)

(defn in
  "Get the contents of a square on the board. Nil if empty"
  [board sq]
  (let [p (get-in board (to-board-ks sq))]
    (if (valid-piece? p) p)))

(defn put
  "Puts a piece at a square in the board"
  [board sq p]
  (assoc-in board (to-board-ks sq) p))

(defn move
  "Moves a piece from one square to the other"
  [board from-sq to-sq]
  (if-let [p (in board from-sq)]
    (-> board
        (put to-sq p)
        (put from-sq empty-sq))))

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
