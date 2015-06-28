(ns cljs.chess.components-test
  (:require-macros [cemerick.cljs.test
                   :refer [is deftest with-test run-tests testing test-var done]]
                   [dommy.core :refer [sel1]]
                   [cljs.core.async.macros :refer [go]])
  (:require [cemerick.cljs.test :as t]
            [dommy.core :as dommy]
            [chess.components :as components]
            [chess.board :refer [start-position]]
            [om.core :as om :include-macros true]))

(defn new-node [id]
  (-> (dommy/create-element "div")
      (dommy/set-attr! "id" id)))

(defn append-node [node]
  (dommy/append! (sel1 js/document :body) node))

(defn container!
  ([] (container! (gensym)))
  ([id]
    (-> id
        new-node
        append-node)
    (. js/document getElementById id)))

(defn click-on! [root selectors]
  (let [elem (sel1 root selectors)]
    (let [e (.createEvent js/document "MouseEvents")]
      (.initMouseEvent e "click" true true js/window 1 0 0)
      (.dispatchEvent elem e))))

(deftest test-container
         (let [c (container! "container-1")]
           (is (sel1 :#container-1))))

(deftest test-square-black-piece
         (let [c (container!)]
           (om/root components/square ["a4" :r true] {:target c})
           (is (sel1 c ["#a4.square.selected" ".piece.black.rook"]))))

(deftest test-square-white-piece
         (let [c (container!)]
           (om/root components/square ["a7" :P false] {:target c})
           (is (sel1 c ["#a7.square" ".piece.white.pawn"]))))

(deftest test-board
         (let [c (container!)]
           (om/root components/board {:board start-position} {:target c})
           (is (sel1 c [:#a1 ".piece.white.rook"]))
           (is (sel1 c [:#h8 ".piece.black.rook"]))))

(deftest ^:async test-board-interaction
         (let [c (container!)]
           (om/root components/board {:board start-position} {:target c})
           (click-on! c "#d2")
           (click-on! c "#d4")
           (js/setTimeout
             (fn []
               (is (sel1 c ["#d4" ".piece.white.pawn"]))
               (is (not (sel1 c ["#d2" ".piece.white.pawn"])))
               (done))
             500)))



