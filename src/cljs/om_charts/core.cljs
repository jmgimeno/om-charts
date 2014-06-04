(ns om-charts.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state (atom {:width  600
                      :height 300
                      :data   [30 10 5 8 15 10]
                      :color  "cornflowerblue"}))

(defn om-bar [data owner]
  (om/component
    (dom/rect #js {:fill   (:color data)
                   :width  (:width data)
                   :height (:height data)
                   :x      (:offset data)
                   :y      (- (:availableHeight data) (:height data))})))

(defn om-dataseries [data owner]
  (om/component
    (let [yScale (-> js/d3 .-scale .linear
                     (.domain #js [0 (apply max (:data data))])
                     (.range #js [0 (:height data)]))
          xScale (-> js/d3 .-scale .ordinal
                     (.domain (apply array (range (count (:data data)))))
                     (.rangeRoundBands #js [0 (:width data)] 0.05))
          bars (map-indexed (fn [i point] {:height          (yScale point)
                                           :width           (.rangeBand xScale)
                                           :offset          (xScale i)
                                           :availableHeight (:height data)
                                           :color           (:color data)})
                            (:data data))]
      (apply dom/g nil
             (om/build-all om-bar bars)))))

(defn om-chart [data owner]
  (om/component
    (dom/svg #js {:width (:width data) :height (:height data)}
             (om/build om-dataseries data))))

(defn om-root [data owner]
  (om/component
    (dom/div nil
             (dom/h1 nil "Chart rendered by Om")
             (om/build om-chart data))))

(om/root
  om-root
  app-state
  {:target (. js/document (getElementById "om-rendered"))})

(defn update-d3 [data]
  (let [yScale (-> js/d3 .-scale .linear
                   (.domain #js [0 (apply max (:data data))])
                   (.range #js [0 (:height data)]))
        xScale (-> js/d3 .-scale .ordinal
                   (.domain (apply array (range (count (:data data)))))
                   (.rangeRoundBands #js [0 (:width data)] 0.05))]
    (-> js/d3 (.select "#d3-chart")
        (.append "svg")
        (.attr #js {:width (:width data) :height (:height data)})
        (.selectAll "bar")
        (.data (clj->js (:data data)))
        .enter (.append "rect")
        (.style "fill" "steelblue")
        (.attr "x" (fn [d i] (xScale i)))
        (.attr "y" (fn [d] (- (:height data) (yScale d))))
        (.attr "width" (.rangeBand xScale))
        (.attr "height" (fn [d] (yScale d))))))

(defn d3-chart [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      (update-d3 data))
    om/IDidUpdate
    (did-update [this prev-props prev-state]
      (update-d3 data))
    om/IRender
    (render [this]
      (dom/div #js {:width (:width data) :height (:height data) :id "d3-chart"}))))

(defn d3-root [data owner]
  (om/component
    (dom/div nil (dom/h1 nil "Chart rendered by d3")
             (om/build d3-chart data))))

(om/root
  d3-root
  app-state
  {:target (. js/document (getElementById "d3-rendered"))})