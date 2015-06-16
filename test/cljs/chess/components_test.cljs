(ns cljs.chess.components-test
  (:require-macros [cemerick.cljs.test
                   :refer [is deftest with-test run-tests testing test-var]])
  (:require [cemerick.cljs.test :as t]))

(deftest should-fail
         (is (= 1 7)))
