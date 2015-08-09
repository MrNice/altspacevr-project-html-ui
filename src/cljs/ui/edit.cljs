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

(defonce current (atom {:title "" :creator "" :text "" :members [] :type "private"}))
;; Actions
(defn cancel [index]
  (let [url (str "#/")]
    (js/window.location.assign url)))

(defn save [index]
  (js/console.log index))

(defn delete [index]
  (remove-space! index app-state)
  (js/window.location.assign "#/"))

(defn save-space! []
  "Saves the current temp space into the DB atom
   Beware of this tight coupling"
  (let [index (int (session/get :current-space))]
    (swap! app-state update-in [:spaces index key] #(merge current %))))

(defn set-space-value! [key value]
  "Sets a key on the current space, used by the on-change events"
  (swap! current assoc-in [key] value))

;; EDIT PAGE
(defn edit-line [title value key]
  "An input-text with a label"
  [h-box
    :children
      [[box :justify :end :size "1" :child [:span.descriptor title]]
       [box :size "6" :child [input-text :model value :width "600px" :on-change #(set-space-value! key %)]]]])

(defn edit-box [title value key rows]
  [h-box
    :children
      [[box :justify :end :size "1" :child [:span.descriptor title]]
       [box :size "6" :child [input-textarea :rows (if rows rows 3) :width "600px" :model value :on-change #(set-space-value! key %)]]]])

(defn space-type-selector [type]
  (let [current (atom "welcome")])
  [h-box
    :children
      [[box :size "1" :child [:div]]
      [v-box
         :size "6"
         :children
           (interpose [gap :size "15px"]
             [[radio-button :model "welcome"  :value current :label "Welcome"  :on-change #(set-space-value! :type "welcome")]
              [radio-button :model "private"  :value current :label "Private"  :on-change #(set-space-value! :type "private")]
              [radio-button :model "featured" :value current :label "Featured" :on-change #(set-space-value! :type "featured")]])]]])

(defn edit-page []
  ;; TODO (Nicholas): Clean-up this let block
  (let [index (int (session/get :current-space))
        space (if-not (pos? index) {:title "" :creator "" :text "" :members [] :type "private"}
                               (nth (:spaces @app-state) index))]
    (js/console.log (pr-str space index))
    (reset! current space)
    (let [{:keys [title creator text members type]} @current]
      (js/console.log type)
      [container "edit-page"
       (interpose [gap :size "20px"] [
        [:h1 (str "Altspace Spaces Admin - " (if title title "Unnamed"))]
        [edit-line "Title" title :title]
        [edit-box "Description" text :text]
        [space-type-selector type]
        [edit-box "Members" (apply str (interpose "\n" members)) :members (count members)]
        [h-box
          :class "edit-delete-cancel-save"
          :justify :between
          :children
            [[:div.delete (if (pos? index) [button :label "Delete" :on-click #(delete index) :class "btn-danger"])]
             [:div.aligner
               [:div.cancel-save
                 [button :label "Cancel" :on-click #(cancel index) :class "btn-default cancel"]
                 [button :label "Save"   :on-click #(save index)   :class "btn-primary"]]]]]])])))
