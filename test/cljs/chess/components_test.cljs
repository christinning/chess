(ns cljs.chess.components-test
  (:require-macros [cemerick.cljs.test
                   :refer [is deftest with-test run-tests testing test-var]]
                   [dommy.core :refer [sel1]])
  (:require [cemerick.cljs.test :as t]
            [dommy.core :as dommy]
            [chess.components :as c]
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

(deftest test-container
         (let [c (container! "container-1")]
           (is (sel1 :#container-1))))

(deftest test-square
         (let [c (container!)]
           (om/root c/square ["a4" :R false] {:target c})
           (is (sel1 [:#a4 ".white.rook.piece"]))))



