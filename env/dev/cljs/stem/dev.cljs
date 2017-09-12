(ns ^:figwheel-no-load stem.dev
  (:require
    [stem.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
