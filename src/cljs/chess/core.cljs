(ns chess.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def initial-board
  [[\r \n \b \q \k \b \n \r]
   [\p \p \p \p \p \p \p \p]
   [\. \. \. \. \. \. \. \.]
   [\. \. \. \. \. \. \. \.]
   [\. \. \. \. \. \. \. \.]
   [\. \. \. \. \. \. \. \.]
   [\P \P \P \P \P \P \P \P]
   [\R \N \B \Q \K \B \N \R]])

(defonce app-state (atom {:board initial-board}))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/h1 nil (:text app)))))
    app-state
    {:target (. js/document (getElementById "app"))}))
