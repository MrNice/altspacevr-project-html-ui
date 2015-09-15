(ns ui.model
  (:require [reagent.core :refer [atom]]
            [reagent.session :as session]))

(defonce app-state (atom
 {:spaces [{:title   "Welcome Space"
            :id      0
            :creator 0
            :type    "welcome"
            :members [1 4 2 3]
            :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}
           {:title   "Nicholas's Hovel"
            :id      1
            :creator 1
            :type    "private"
            :members [1]
            :text    "This is where clojurescript junkies tend to fester"}
           {:title   "Origin Featured Space"
            :id      2
            :creator 7
            :type    "standard"
            :members [1 7 2 6 3]
            :text    "Welcome to Altspace! Usasdfkhaksjdhfgkjashgdfjkhasdgfe this space to get acquainted with the interface"}
           {:title   "USS Enterprise"
            :id      3
            :creator 9
            :type    "featured"
            :members [5 7 6 8]
            :text    "To boldy go, where no man has gone before. Bacon ipsum dolor amet cupim pork chop ham hock kevin filet mignon flank prosciutto spare ribs porchetta tri-tip tail swine ham frankfurter meatball. Jerky alcatra hamburger, meatball kielbasa corned beef fatback pork loin shankle leberkas swine spare ribs pork chop picanha ham. Pork belly tail tenderloin short loin, picanha alcatra doner ball tip meatball filet mignon. Ribeye doner t-bone, kevin porchetta turducken tenderloin meatloaf pork prosciutto capicola tri-tip jerky alcatra shoulder.\nMeatball porchetta shoulder venison pastrami. Strip steak sausage swine drumstick tail boudin tri-tip ground round hamburger shank fatback pork chop cupim chuck. Bacon ground round pig beef ribs t-bone doner. Bresaola pastrami pancetta cow porchetta tenderloin kevin pig pork beef doner beef ribs. Cupim ball tip tongue shoulder, frankfurter pork chop salami. Tongue ball tip kevin, ground round strip steak shank capicola drumstick jowl tenderloin biltong."}]
  :members [{:id 0 :name "Admin Istrator"        :admin true  :gender "none"}
            {:id 1 :name "Nicholas van de Walle" :admin true  :gender "male"}
            {:id 2 :name "Cymatic Bruce"         :admin false :gender "male"}
            {:id 4 :name "Ari"                   :admin false :gender "male"}
            {:id 3 :name "Dana"                  :admin false :gender "female"}
            {:id 5 :name "Spock"                 :admin false :gender "male"}
            {:id 6 :name "Uhura"                 :admin false :gender "female"}
            {:id 7 :name "Kirk"                  :admin true  :gender "male"}
            {:id 8 :name "Scottie"               :admin false :gender "male"}
            {:id 9 :name "Matt Jeffries"         :admin true  :gender "male"}]}))

;; Queries
(defn get-member [id]
  (first (filter #(= id (:id %)) (:members @app-state))))

;; Helpers
(defn positions
  "Finds the current indexes of items in a collection which
   pass a predicate. Used to locate spaces for updating"
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                coll))

;; TODO (Nicholas): cache this value, have it only be computed once
(defn gen-space-id [spaces]
  "Give us the next available space ID"
  (inc (apply max (map :id spaces))))

(defn auth [id]
  "Is this member an administrator?"
  (:admin (get-member id)))

;; Constructors
(defn make-space []
 {:id (inc (apply max (map :id (:spaces @app-state))))
  :text ""
  :title ""
  :members []
  :type "standard"
  :creator (session/get :current-user)})

;; Mutations
(defn add-space! [space]
  "Add an ID 1 higher than the current max space id,
   and put it at the end of :spaces"
  (let [to-save (assoc space :id (gen-space-id (:spaces @app-state)))]
    (swap! app-state assoc-in [:spaces] (conj (:spaces @app-state) to-save))))

(defn update-space! [space]
  "Saves the current temp space into the DB atom
   Beware of this tight coupling, uses current index
   as well as the ID. Consider turning :spaces into a
   map instead of a vector"
  (let [index (first (positions #(= (:id space) (:id %)) (:spaces @app-state)))]
    (swap! app-state update-in [:spaces index] #(merge % space))))

(defn remove-space! [id]
  "filter the space out by id"
  (swap! app-state assoc-in [:spaces] (filterv #(not= id (:id %)) (:spaces @app-state))))
