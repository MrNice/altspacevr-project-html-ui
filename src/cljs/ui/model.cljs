(ns ui.model
  (:require [reagent.core :refer [atom]]))

;; TODO (Nicholas) update members to use member IDs instead
;; Store member data in its own model atom
(defonce app-state (atom
  {:spaces [{:title   "Welcome Space"
             :creator "Admin Istrator"
             :type    "welcome"
             :members ["Nicholas" "Dana" "Bruce" "Ari"]
             :text    "Welcome to Altspace! Use this space to get acquainted with the interface"}
            {:title   "Nicholas's hovel"
             :creator "Nicholas"
             :type    "private"
             :members ["Nicholas" "Dana" "Cymatic Bruce" "Ari"]
             :text    "This is where clojurescript junkies tend to fester"}
            {:title   "Origin Featured Space"
             :creator "Kirk"
             :type    "featured"
             :members ["Nicholas" "Dana" "Cymatic Bruce" "Ari"]
             :text    "Welcome to Altspace! Usasdfkhaksjdhfgkjashgdfjkhasdgfe this space to get acquainted with the interface"}
            {:title   "USS Enterprise"
             :creator "Matt Jeffries"
             :type    "welcome"
             :members ["Spock" "Kirk" "Uhura" "Scottie"]
             :text    "To boldy go, where no man has gone before. Bacon ipsum dolor amet cupim pork chop ham hock kevin filet mignon flank prosciutto spare ribs porchetta tri-tip tail swine ham frankfurter meatball. Jerky alcatra hamburger, meatball kielbasa corned beef fatback pork loin shankle leberkas swine spare ribs pork chop picanha ham. Pork belly tail tenderloin short loin, picanha alcatra doner ball tip meatball filet mignon. Ribeye doner t-bone, kevin porchetta turducken tenderloin meatloaf pork prosciutto capicola tri-tip jerky alcatra shoulder.\nMeatball porchetta shoulder venison pastrami. Strip steak sausage swine drumstick tail boudin tri-tip ground round hamburger shank fatback pork chop cupim chuck. Bacon ground round pig beef ribs t-bone doner. Bresaola pastrami pancetta cow porchetta tenderloin kevin pig pork beef doner beef ribs. Cupim ball tip tongue shoulder, frankfurter pork chop salami. Tongue ball tip kevin, ground round strip steak shank capicola drumstick jowl tenderloin biltong."}]}))
