(ns cljs.chess.components-test
  (:require-macros [cemerick.cljs.test
                   :refer [is deftest with-test run-tests testing test-var]]
                   [dommy.core :refer [sel1]])
  (:require [cemerick.cljs.test :as t]
            [dommy.core :as dommy]
            [chess.components :as c]
            [chess.board :as board]
            [om.core :as om :include-macros true]))

(defn new-node [id]
  (-> (dommy/create-element "div")
      (dommy/set-attr! "id" id)))

(defn append-node [node]
  (dommy/append! (sel1 js/document :body) node))

(defn container! []
  (-> gensym
      new-node
      append-node))

(comment (deftest initial-board
                  (let [container (container!)]
                    (om/root c/board {} {:target container})
                    (is (= 1 2)))))


(deftest fails
         (is (= 1 3)))