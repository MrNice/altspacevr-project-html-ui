(ns ui.core
    (:require [reagent.core    :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [ui.model :refer [app-state]]
              [ui.home  :refer [home-page]]
              [ui.edit  :refer [edit-page]])
    (:import goog.History))

;; (TODO: Nicholas): Add user manangement & protect routes with auth
(session/put! :current-user 1)

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/edit/:id" {:as params}
  (session/put! :current-space (first
                                 (filter #(= (int (:id params)) (:id %))
                                          (:spaces @app-state))))
  (session/put! :creating false)
  (session/put! :current-page #'edit-page))

(secretary/defroute "/create" []
  (session/put! :creating true)
  (session/put! :current-page #'edit-page))

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
