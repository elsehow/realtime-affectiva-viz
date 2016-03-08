(ns npm-cljs.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [npm-cljs.core :as core])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defonce data-schema
  {:date-and-time
   :timestamp
   :expressions [:innerBrowRaise
                 :eyeClosure
                 :lipPucker
                 :smirk
                 :browFurrow
                 :mouthOpen
                 :chinRaise
                 :lipCornerDepressor
                 :noseWrinkle
                 :lipSuck
                 :smile
                 :attention
                 :lipPress
                 :browRaise]
   :emotions   [:engagement
                :valence
                :surprise
                :joy
                :contempt
                :anger
                :disgust
                :sadness
                :fear]})

;; example data

(defonce example-data
  (js->clj window.exampledata :keywordize-keys true))

(defn htmlify [hmap]
  (.stringify js/JSON (clj->js hmap) nil 2))

(defn get-over-time [path data]
  (map #(get-in % path) data))

(defn list-data [key data]
  (map
    #(get-over-time [key %] data)
    (key data-schema)))

(defn data-over-time [key-of-list data]
  (zipmap
    (key-of-list data-schema)
    (list-data key-of-list data)
    ))

(defcard-rg first-card
  [:div>pre (htmlify
   (data-over-time :expressions example-data)
    )])

(defcard-rg home-page-card
  [core/home-page])
     
(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
