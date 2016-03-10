(ns npm-cljs.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [npm-cljs.core :as core])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defcard-rg webcam-card
  [core/setup-webcam-and-view])

;(defcard-rg graph-card
;  [core/expression-graph :expressions])

(defcard-rg home-page-card
  [core/home-page])

(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
