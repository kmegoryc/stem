(ns stem.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defn wrap-middleware [handler]
  (-> handler
    (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
