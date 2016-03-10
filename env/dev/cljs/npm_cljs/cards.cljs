(ns npm-cljs.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [npm-cljs.core :as core])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))

(defn get-webcam [errcb successcb]
  (window.getusermedia
   #js {:video true :audio false}
   (fn [err stream]
     (if err
       (errcb err)
       (successcb stream)))))

(defn get-and-show-webcam []
  (let [webcam-div (.getElementById js/document "webcam")]
    (get-webcam
     #(println "ERR!" %)
     #(window.attachmediastream % webcam-div
       ))))

(defcard-rg webcam-card
  (reagent/create-class
   {:reagent-render (fn [] [:video
                           {:id "webcam"}])
    :component-did-mount get-and-show-webcam})) 

;(defcard-rg graph-card
;  [core/expression-graph :expressions])

(defcard-rg home-page-card
  [core/home-page])

(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
