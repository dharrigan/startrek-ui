(ns ^:figwheel-hooks startrek.ui.main
  (:require
   [ajax.core :refer [GET]]
   [ant-man.core :as ant-man]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]))

(defonce starships (r/atom nil))
(defonce starship-data (r/atom nil))
(defonce modal-visible (r/atom false))
(defonce route-match (r/atom nil))

;; If running in a container, or deployed as as service with a proxy in front,
;; then uncomment this line and comment the other backend-url line. There is
;; an example of running Traefik as a reverse-proxy in the scripts directory.
(def backend-url "/api/starships")

;; If running locally, with both this UI and the starship project that serves
;; up the data, then use the line below so that this frontend can talk to the
;; backend on the same machine.
;(def backend-url "http://localhost:8080/api/starships")

(defn to-clj
  [data]
  (js->clj data :keywordize-keys true))

(defn fetch-starships
  [data]
  (GET backend-url {:handler #(reset! data %)
                    :finally #(ant-man/message {:level :success
                                                :content "Loaded"
                                                :duration 2})}))

(defn sorter
  [data1 data2 field]
  (compare (field (to-clj data1))
           (field (to-clj data2))))

(defn render-starship-modal
  []
  (fn []
    [ant-man/modal {:title (:registry @starship-data)
                    :width 600
                    :visible @modal-visible
                    :on-ok #(reset! modal-visible false)
                    :on-cancel #(reset! modal-visible false)}
     [:div.starship>img {:src (str "images/" (:image @starship-data))}]]))

(defn on-row
  [record]
  #js {:onClick #((reset! starship-data (to-clj record))
                  (reset! modal-visible true))})

(defn render-starships
  []
  (fetch-starships starships)
  (fn []
    [ant-man/table
     {:dataSource @starships
      :row-key "id"
      :columns [{:title :Registry :dataIndex :registry :sorter #(sorter %1 %2 :registry)}
                {:title :Captain :dataIndex :captain :sorter #(sorter %1 %2 :captain)}
                {:title :Class :dataIndex :class :sorter #(sorter %1 %2 :class)}
                {:title :Launched :dataIndex :launched :sorter #(sorter %1 %2 :launched)}
                {:title :Affliation :dataIndex :affiliation :sorter #(sorter %1 %2 :affiliation)}]
      :on-row #(on-row %)}]))

(defn render-breadcrumb
 []
 [ant-man/breadcrumb {:style {:margin "16px 0"}}
  [ant-man/breadcrumb-item "Home"]
  [ant-man/breadcrumb-item "Starships"]])

(defn render-header
  []
  [ant-man/layout-header
   [ant-man/menu {:theme :dark :mode :horizontal :style {:lineHeight "64px"}}
    [ant-man/menu-sub-menu {:title "File"}
     [ant-man/menu-item {:onClick #(rfe/push-state ::homepage)} "Home"]
     [ant-man/menu-item {:onClick #(rfe/push-state ::starships)} "Starships"]]
    [ant-man/menu-item {:onClick #(js/alert "Live Long And Prosper!")} "About"]]])

(defn render-footer
  []
  [ant-man/layout-footer {:style {:textAlign "center"}} "United Federation of Planets"])

(defn starships-page
  []
  [ant-man/layout
   [render-header]
   [ant-man/layout-content {:style {:padding "0 50px"}}
    [render-breadcrumb]
    [render-starships]
    [render-starship-modal]
    [render-footer]]])

(defn home-page
  []
  [ant-man/layout
   [render-header]
   [ant-man/layout-content {:style {:padding "0 50px"}}
    [ant-man/row {:justify :center}
     [ant-man/col
      [:div>img {:style {:max-width "100%"
                         :max-height "100%"
                         :height "auto"
                         :display :table
                         :margin "0 auto"} :src "images/united-federation-of-planets.png"}]]]
    [render-footer]]])

(defn landing-page
  []
  (when@route-match
    (let [view (-> @route-match :data :view)]
      [view @route-match])))

(def routes
  [["/" {:name ::homepage :view home-page}]
   ["/starships" {:name ::starships :view starships-page}]])

(def router
 (rf/router routes {:data {:coercion rss/coercion}}))

(defn on-navigate
  [m]
  (reset! route-match m))

(defn mount
  []
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}) ; I prefer no hashes...thank you!
  (rdom/render [landing-page]
               (js/document.getElementById "app")))

(defn ^:after-load remount
  []
  (mount))

(defonce engage (do (mount) true))
