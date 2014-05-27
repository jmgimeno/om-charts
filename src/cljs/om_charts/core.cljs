(ns om-charts.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [strokes :refer [d3]]))

(enable-console-print!)

(def app-state (atom {:text   "Bar chart rendered by Om"
                      :width  600
                      :height 300
                      :data   [30 10 5 8 15 10]
                      :color  "cornflowerblue"}))

(defn bar [data owner]
  (om/component
    (dom/rect #js {:fill   (:color data)
                   :width  (:width data)
                   :height (:height data)
                   :x      (:offset data)
                   :y      (- (:availableHeight data) (:height data))})))

(defn dataseries [data owner]
  (om/component
    (let [yScale (-> d3 .-scale .linear
                     (.domain #js [0 (apply max (:data data))])
                     (.range #js [0 (:height data)]))
          xScale (-> d3 .-scale .ordinal
                     (.domain (apply array (range (count (:data data)))))
                     (.rangeRoundBands #js [0 (:width data)] 0.05))
          bars (map-indexed (fn [i point] {:height          (yScale point)
                                           :width           (.rangeBand xScale)
                                           :offset          (xScale i)
                                           :availableHeight (:height data)
                                           :color           (:color data)})
                            (:data data))]
      (apply dom/g nil
             (om/build-all bar bars)))))

(defn chart [data owner]
  (om/component
    (dom/svg #js {:width (:width data) :height (:height data)}
             (om/build dataseries data))))

(defn root [data owner]
  (om/component
    (dom/div nil
             (dom/h1 nil (:text data))
             (om/build chart data))))

(om/root
  root
  app-state
  {:target (. js/document (getElementById "app"))})
