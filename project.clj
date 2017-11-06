(defproject stem "0.1.0-SNAPSHOT"
  :description "Live feedback system"
  :url "https://github.com/kmegoryc/stem"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [garden "1.3.2"]
                 [ring-server "0.4.0"]
                 [reagent "0.7.0"]
                 [reagent-utils "0.2.1"]
                 [ring "1.6.2"]
                 [ring-middleware-format "0.7.2"]
                 [ring/ring-defaults "0.3.1"]
                 [resource-seq "0.2.0"]
                 [compojure "1.6.0"]
                 [cljs-ajax "0.6.0"]
                 [org.clojure/core.async "0.3.443"]
                 [hiccup "1.0.5"]
                 [http-kit "2.2.0"]
                 [thinktopic/think.semantic-ui "0.1.71.0-0"]
                 [thinktopic/greenhouse "0.1.1"]
                 [yogthos/config "0.9"]
                 [org.clojure/clojurescript "1.9.908"
                  :scope "provided"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.2.0"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.5"]
            [lein-heroku "0.5.3"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :heroku {:app-name "stem-feedback"
           :jdk-version "1.8"
           :include-files ["target/stem.jar"]
           :process-types { "web" "java -jar target/stem.jar"}}

  :ring {:handler stem.handler/app
         :uberwar-name "stem.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "stem.jar"

  :main stem.server

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to        "target/cljsbuild/public/js/app.js"
              :output-dir       "target/cljsbuild/public/js"
              :source-map       "target/cljsbuild/public/js/app.js.map"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:on-jsload "stem.core/mount-root"}
             :compiler
             {:main "stem.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}
            }
   }

  :garden
  {:builds [{:source-paths ["src/clj"]
             :stylesheet stem.styles.core/styles
             :compiler {:output-to "resources/public/css/site.css"
                        :pretty-print? true}}]}

  :figwheel
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7002
   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
   :css-dirs ["resources/public/css"]
   :ring-handler stem.handler/app}



  :profiles {:dev {:repl-options {:init-ns stem.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[binaryage/devtools "0.9.4"]
                                  [ring/ring-mock "0.3.1"]
                                  [ring/ring-devel "1.6.2"]
                                  [prone "1.1.4"]
                                  [figwheel-sidecar "0.5.13"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [pjstadig/humane-test-output "0.8.2"]]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.13"]
                             [lein-garden "0.3.0"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["garden" "once"] ["cljsbuild" "once" "min"] "minify-assets"]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
