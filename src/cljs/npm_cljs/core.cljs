(ns npm-cljs.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))
;; testing stuff

(defn htmlify [clj-type]
  (let [stringify #(.stringify js/JSON (clj->js %) nil 2)]
    [:div>pre (stringify clj-type)]))

;; app state

(defonce store (atom nil))

(def aff-api-endpoint
  "http://verdigris.ischool.berkeley.edu:3334")

(def chunk-time
  1000)

(defn get-webcam [errcb successcb]
  (window.getusermedia
   #js {:video true :audio false}
   (fn [err stream]
     (if err
       (errcb err)
       (successcb stream)))))

(defn setup-chunk-upload [stream]
  (let [chunk
        (window.chunky stream chunk-time aff-api-endpoint)
        socket (.-socket chunk)]
    (.on socket "data"
         #(reset! store (js->clj % :keywordize-keys true))
    (.on socket "error"
         #(println "socket error " %))
    )))

(defn get-and-show-webcam []
  (let [webcam-div
        (.getElementById js/document "webcam")]
    (get-webcam
     #(println "ERR!" %)
     (fn [stream]
       (window.attachmediastream stream webcam-div)
       (setup-chunk-upload stream)
       ))))

(defn setup-webcam-and-view []
  (reagent/create-class
   {:reagent-render (fn [] [:video
                           {:id "webcam"
                            :style {:height 300}}
                            ])
    :component-did-mount get-and-show-webcam}))



;; -------------------------
;; Views


(def named-html-colors
  "These are just some of my favs. w3.org has a complete list:https://www.w3.org/TR/SVG/types.html#ColorKeywords"
  ["lightyellow"
   "lime"
   "linen"
   "magenta"
   "maroon"
   "mediumaquamarine"
   "mediumblue"
   "mediumpurple"
   "mediumseagreen"
   "mediumslateblue"
   "mediumspringgreen"
   "dodgerblue"
   "purple"
   "orchid"])

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

;(defonce example-data
;  (js->clj window.exampledata :keywordize-keys true))


(defn get-over-time [data & keys]
  (map-indexed
   #(hash-map
     :y (get-in %2 keys)
     :x %1
     )
   data))

(defn list-data [key data]
  (map
   #(hash-map
     :values (get-over-time data key %1)
     :key %1
     :color %2)
   (key data-schema)
   (cycle named-html-colors)
   ))

(defn data-over-time [key-of-list data]
  (zipmap
    (key-of-list data-schema)
    (list-data key-of-list data)
    ))


; d3 stuff

(defn graph-div-did-mount [div-id my-data]
  (fn []
    (.addGraph js/nv (fn []
                       (let [chart (.. js/nv -models lineChart
                                       (margin #js {:left 100})
                                       (useInteractiveGuideline true)
                                       (transitionDuration 10)
                                       (showLegend true)
                                       (showYAxis true)
                                       (showXAxis true))]
                         (.. chart -xAxis 
                             (axisLabel "time") )
                         (.. chart -yAxis 
                             (axisLabel "emotion") 
                             (tickFormat (.format js/d3 ",r")))
                         (.. js/d3 (select (str "#" div-id " svg"))
                             (datum (clj->js my-data))
                             (call chart))
                         (println (.update chart))
                         )))
    ))

(defn d3-svg [div-id]
  (fn []
    [:div
     {:id div-id
     :style {:width "750" :height "260"}}
     [:svg ]]))

(defn expression-graph [keyword data]
  (let [expressions (data-over-time keyword data)]
    (reagent/create-class {:reagent-render (d3-svg
                                            (name keyword))
                           :component-did-mount
                           (graph-div-did-mount
                            (name keyword)
                            (vals expressions))
                           })))


(defn home-page []
  [:div
   [setup-webcam-and-view]
   (if @store
     [expression-graph :emotions @store]
     [:p "waiting for data! please hold.."]
     )
     ])
;
;   [:h2 "Welcome "]
;   [:div [:a {:href "/about"} "go to about page"]]])



;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
