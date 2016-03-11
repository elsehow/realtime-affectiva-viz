(ns npm-cljs.cards
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [npm-cljs.core :as core])
  (:require-macros
   [devcards.core
    :as dc
    :refer [defcard defcard-doc defcard-rg deftest]]))
; old views
;(defcard-rg webcam-card
;  [core/setup-webcam-and-view])
;(defcard-rg graph-card
;  [core/expression-graph :expressions])
;(defcard-rg home-page-card
;  [core/home-page])

(defn series []
  #js [
   #js {:color "steelblue"
        :data #js [
                   #js {:x 0 :y 40}
                   #js {:x 1 :y 20 }
                   #js {:x 2 :y 20 }
                   #js {:x 3 :y 20 }
                   #js {:x 4 :y 20 }
                   ]
        }
   ]
)

(defn setup-graph [div-id]
  (let [Graph (.-Graph window.rickshaw)]
    (fn []
      (.render
       (Graph. #js {:element (.getElementById js/document div-id)
                    :width 500
                    :height 200
                    :renderer "line"
                    :series (series)
                    }
               ))
      )
    )
  )


(defn rickshaw-div [div-id]
  (fn [] [:div {:id div-id}]))

(defn expression-graph [div-id]
  (reagent/create-class {:reagent-render (rickshaw-div div-id)
                         :component-did-mount (setup-graph div-id)
                         }
                        )
  )
  

(defcard-rg graph-card
[expression-graph "graph"]
;[:div "graph"]
)

(reagent/render [:div] (.getElementById js/document "app"))

;; remember to run 'lein figwheel devcards' and then browse to
;; http://localhost:3449/cards
