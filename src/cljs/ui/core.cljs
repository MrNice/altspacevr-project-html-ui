(ns ui.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [re-com.core :refer [h-box v-box box]]
              [re-com.buttons :refer [button]]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; Views

(defn space-title [name creator]
  [:span.title [:span.name name] [:span (str "created by " creator)]])

(defn space-component [{:keys [name creator text]} space]
  [v-box
    :class "space"
    :children [[space-title name creator]
               [:p.text text]
               [h-box
                 :children [[button
                              :label "Edit"
                              :tooltip "Change this space"
                              :tooltip-position :left-center
                              :class "btn btn-default edit"]]
                 :justify :end]]])

(defn home-page []
  [v-box
    :align :center
    :gap "20px"
    :children [[:h1 "Altspace Spaces Admin"]
              [space-component {:name    "Welcome Space"
                                :creator "Admin Istrator"
                                :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}]
              [space-component {:name    "Welcome Space"
                                :creator "Admin Istrator"
                                :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}]
              [space-component {:name    "Welcome Space"
                                :creator "Admin Istrator"
                                :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}]
              [h-box
                :children [[button :label "Create"]]
                :justify :end]
              [:div [:a {:href "#/about"} "go to about page"]]]])

(defn about-page []
  [:div [:h2 "About ui"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
