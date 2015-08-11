(ns ui.model
  (:require [reagent.core :refer [atom]]))

;; TODO (Nicholas) update members to use member IDs instead
;; Store member data in its own model atom
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
            {:id 3 :name "Ari"                   :admin false :gender "male"}
            {:id 4 :name "Dana"                  :admin false :gender "female"}
            {:id 5 :name "Spock"                 :admin false :gender "male"}
            {:id 6 :name "Uhura"                 :admin false :gender "female"}
            {:id 7 :name "Kirk"                  :admin false :gender "male"}
            {:id 8 :name "Scottie"               :admin false :gender "male"}
            {:id 9 :name "Matt Jeffries"         :admin true  :gender "male"}]}))

;; Mutations
(defn add-space! [space]
  "Add an ID 1 higher than the current max space id,
   and put it at the end of :spaces"
  (let [to-save (assoc space :id (inc (apply max (map :id (:spaces @app-state)))))]
    (swap! app-state assoc-in [:spaces] (conj (:spaces @app-state) to-save))))

(defn remove-space! [id]
  "filter the space out by id"
  (swap! app-state assoc-in [:spaces] (filterv #(not= id (:id %)) (:spaces @app-state))))

;; Queries
(defn get-member [id]
  (first (filter #(= id (:id %)) (:members @app-state))))
