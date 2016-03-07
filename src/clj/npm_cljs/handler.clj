(ns npm-cljs.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [npm-cljs.middleware :refer [wrap-middleware]]
            [environ.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(def loading-page
  (html5
   [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))]
    [:body
     mount-target
     (include-js "/js/deps-bundle.js")
     (include-js "/js/app.js")]))

(def cards-page
  (html5
   [:head
     [:meta {:charset "utf-8"}]]
    [:body
     mount-target
     (include-js "/js/app_devcards.js")]))

(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  (GET "/cards" [] cards-page)
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
