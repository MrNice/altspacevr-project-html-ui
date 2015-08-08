(ns ui.edit
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [re-com.core :refer [h-box v-box box gap]]
            [re-com.buttons :refer [button]]
            [re-com.misc :refer [input-text input-textarea radio-button]]
            [ui.model :refer [app-state]]
            [ui.common :refer [container]])
  (:import goog.History))

(defn remove-space! [index state]
  (js/console.log index)
  (swap! state assoc-in [:spaces]
    (let [spaces (:spaces @state)]
      (js/console.log (pr-str spaces))
      (cond
        (= 1 (count spaces)) []
        (= (dec (count spaces)) index) (subvec spaces 0 index)
        (= index 0) (subvec spaces 1)
        :else (vec (concat (subvec spaces 0 index)
                     (subvec spaces (inc index))))))))

;; Actions
(defn cancel [index]
  (if (= (:name (nth (:spaces @app-state) index)) "") (remove-space! index app-state))
  (let [url (str "#/")]
    (js/window.location.assign url)))

(defn save [index]
  (js/console.log index))

(defn delete [index]
  (remove-space! index app-state)
  (js/window.location.assign "#/"))

;; EDIT PAGE
(defn edit-line [name value]
  "An input-text with a label"
  [h-box
    :children
      [[box :justify :end :size "1" :child [:span.descriptor name]]
       [box :size "6" :child [input-text :model value :width "600px" :on-change #(.log js/console %)]]]])

(defn edit-box [name value rows]
  [h-box
    :children
      [[box :justify :end :size "1" :child [:span.descriptor name]]
       [box :size "6" :child [input-textarea :rows (if rows rows 3) :width "600px" :model value :on-change #(.log js/console %)]]]])

(defn space-type-selector []
  [h-box
    :children
      [[box :size "1" :child [:div]]
      [v-box
         :size "6"
         :children
           [[radio-button :model "5" :value "Welcome"  :label "Welcome"  :on-change #(.log js/console %)]
            [radio-button :model "5" :value "Private"  :label "Private"  :on-change #(.log js/console %)]
            [radio-button :model "5" :value "Featured" :label "Featured" :on-change #(.log js/console %)]]]]])

(defn edit-page []
  (let [index (int (session/get :current-space))]
    (let [{:keys [name creator text members]} (nth (:spaces @app-state) index)]
      [container "edit-page"
        [:h1 (str "Altspace Spaces Admin - " (if name name "Unnamed"))]
        [edit-line "Title" name]
        [edit-box "Description" text]
        [gap :size "20px"]
        [space-type-selector]
        [gap :size "20px"]
        [edit-box "Members" (apply str (interpose ",\n" members)) (count members)]
        [h-box
          :justify :between
          :children
            [      [button :label "Delete" :on-click #(delete index) :class "btn-danger"]
             [:div [button :label "Cancel" :on-click #(cancel index)]
                   [button :label "Save"   :on-click #(save index)   :class "btn-primary"]]]]])))
