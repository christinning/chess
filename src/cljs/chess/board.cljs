(ns chess.board
  (:require [clojure.string :refer [upper-case]]
            [clojure.set :refer [map-invert]]
            [clojure.string :refer [lower-case]]))


;; Board is a virtual 144 element array representing a
;; 12X12 space. The central 64 squares represent the visible
;; chessboard, the edging is used to determine whether a move goes off
;; the board
;; The board is addressed as follows -
;; i, int        - the index in the 144 element array
;; xy, [int int] - the coordinate in the 12X12 space
;; fr, String    - the algebriac notation (file, rank) for a square
;;                 in the chessboard (eg. "a1" [2 2] 26)
(def chessboard-side 8)
(def edging 2)
(def world-side (+ edging chessboard-side edging))

(def empty-sq nil)

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

(defn i->xy [i]
  (let [y (int (/ i world-side))
        x (mod i world-side)]
    [x y]))

(defn xy->i [[x y]]
  (+ x (* world-side y)))

(let [char-to-int (into {}
                        (map-indexed
                          (fn [i c] [c (+ edging i)])
                          (seq "abcdefgh")))
      int-to-char (map-invert char-to-int)]
  (defn x->f [x]
    (int-to-char x))
  (defn f->x [f]
    (char-to-int f)))

(defn y->r [y]
  (if (> (- world-side edging) y (dec edging))
    (- y (dec edging))))

(defn parse-int
  [i]
  (js/parseInt i))

(defn r->y [r]
  (let [ri (parse-int r)]
    (if (>= chessboard-side ri 1)
      (+ ri (dec edging)))))


(defn in-board? [xy]
  (let [max (+ edging chessboard-side)
        min (dec edging)]
    (every? #(> max % min) xy)))

(defn xy->fr [[x y :as xy]]
  (if (in-board? xy)
    (str (x->f x) (y->r y))))

(defn fr->xy [[f r :as fr]]
  [(f->x f) (r->y r)])

(defn i->fr [i]
  (-> i i->xy xy->fr))

(defn fr->i [fr]
  (-> fr fr->xy xy->i))

(defn mirror [i]
  (let [[x y] (i->xy i)
        mirrored-rank (dec (- world-side y))]
    (xy->i [x mirrored-rank])))

(defn offset [i dx dy]
  (+ i dx (* dy world-side)))

(defn squares-and-pieces
  "Get board rows from top to bottom. Returns a seq of seqs
  each containing a vector of [square-name piece].
  Eg. [\"a2\" :p] or [\"a3\" nil]"
  [{board :board}]
  (let [indexes (range edging (+ edging chessboard-side))]
    (for
      [y (reverse indexes)
       x indexes
       :let [xy [x y]]]
      [(xy->fr xy) (board (xy->i xy))])))

(defn in
  "Get the contents of a square on the board. Nil if empty"
  [{board :board} sq]
  (board (fr->i sq)))

(defn put
  "Puts a piece at a square in the board"
  [board sq p]
  (assoc board (fr->i sq) p))

(defn as-rows
  "Converts a sequence of all squares into a sequence of rows."
  [board-seq]
  (partition 8 board-seq))

(defn move
  "Moves a piece from one square to the other"
  [{:keys [board moves] :as game} from-sq to-sq]
   (if-let [p (in game from-sq)]
              {:board (-> board
                          (put from-sq empty-sq)
                          (put to-sq p))
               :moves (conj moves [from-sq to-sq])}
              game))

(def new-game
  (let [a1 (fr->i "a1")
        a2 (fr->i "a2")
        fill-row (fn [sq pieces]
                   (into {} (map-indexed (fn [i p] [(+ sq i) p]) pieces)))
        white (merge
                (fill-row a1 [:R :N :B :Q :K :B :N :R])
                (fill-row a2 (replicate 8 :P)))
        lcase (fn [sym] (-> sym name lower-case keyword))
        black (reduce-kv (fn [m k v] (assoc m (mirror k) (lcase v))) {} white)]

    {:board (merge white black)
     :moves []}))


