(ns chess.board
  (:require [clojure.string :refer [upper-case]]
            [clojure.set :refer [map-invert]]))



(def empty-sq nil)

(def empty-rank (vec (replicate 8 empty-sq)))

(def start-position
  [[:r :n :b :q :k :b :n :r]
   [:p :p :p :p :p :p :p :p]
   empty-rank
   empty-rank
   empty-rank
   empty-rank
   [:P :P :P :P :P :P :P :P]
   [:R :N :B :Q :K :B :N :R]])



(def pieces {:r "rook" :R "rook"
             :n "knight" :N "knight"
             :b "bishop" :B "bishop"
             :q "queen" :Q "queen"
             :k "king" :K "king"
             :p "pawn" :P "pawn"})

(let [f-to-i (into {}
              (map-indexed
               (fn [i c] [c i])
               (seq "abcdefgh")))
      i-to-f (map-invert f-to-i)]
  (do
    (defn file->i [f] (get f-to-i (char f)))
    (defn i->file [i] (get i-to-f i))))

(defn parse-int
  [i]
  (js/parseInt i))

(defn to-board-ks
  "Converts a square reference in the form \"a3\" to the keys to access it in
  board structure, eg. [5 0]"
  [p]
  (let [[f r] (vec p)]
    [(- 8 (parse-int r)) (file->i f)]))

(defn from-board-ks
  "The complement of to-board-ks"
  [x y]
  (str (i->file x) (- 8 y)))

(defn squares-and-pieces
  "Get board rows from top to bottom. Returns a seq of seqs 
  each containing a vector of [square-name piece]. 
  Eg. [\"a2\" :p] or [\"a3\" nil]"
  [board]
  (for
      [r (range 8)
       f (range 8)]
    [(from-board-ks f r) (get-in board [r f])]))

(defn in-rows
  "Converts a sequence of all squares into a sequence of rows."
  [board-seq]
  (partition 8 board-seq))

(defn in
  "Get the contents of a square on the board. Nil if empty"
  [board sq]
  (get-in board (to-board-ks sq)))

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
