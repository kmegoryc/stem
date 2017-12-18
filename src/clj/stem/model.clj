(ns stem.model
  (:require [resource-seq.core :refer [resource-seq]]
            [ring.util.response :refer [redirect content-type resource-response response status]]
            [monger.core :as mongo]
            [monger.collection :as coll]
            [monger.conversion :refer [from-db-object to-object-id]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [config.core :refer [env]]))

#_(def conn (mongo/connect {:host "127.0.0.1" :port 27017})
    ;;(mongo/connect (if (env :dev) "127.0.0.1" "heroku/mongo/")
    )

#_(def db (mongo/get-db conn "monger-test"))

#_(defn uuid
    []
    (str (java.util.UUID/randomUUID)))

;;;; Survey Model

#_(defn get-survey
    [id]
    ;; read one survey out of mongo by id (uuid)
    )


#_(defn create-survey
    [request]
    (let [survey (:params request)]
      (coll/insert-and-return db "surveys" survey)
      ;;(clojure.pprint/pprint (coll/find-maps db "surveys"))
      ;; return updated reord (usually just the return value of insert, cause sometimes it adds data like :last-update)
      ;; (mongo/insert-and-return connection "sureys" new-survey)
      ;; that will return with new :_id in it
      ))

#_(defn read-surveys []
    #_(let [all-surveys (map
                          (fn [cat]
                            (from-db-object cat false))
                          (coll/distinct db coll "surveys"))]
        (clojure.pprint/pprint all-surveys)
        (response all-surveys)))

#_(defn update-survey
    [survey]
    ;; (mongo/update-by-id db "surveys" (:_id survery) surv3ey)
    ;;   (if (mongo/exists? conn "surveys" (:_id survey))
    ;;      If record exists update it
    ;;      (do
    ;;        (mongo/update-by-id db "surveys" (:_id survery) survey)
    ;;        {:success true})
    ;;      {:success false
    ;;       :message "Recrod does not exist"}
    ;;
    )

(defn read-edn []
  (->> (resource-seq)
    (filter #(and (.contains (first %) "public/data") (.contains (first %) ".edn")))
    (map (fn [[path reader-fn]]
           (with-open [r (reader-fn)] (edn/read-string (slurp r)))))
    (first)))

(defn write-edn [edn-updated]
  (spit "resources/public/data/modules.edn" edn-updated))

(defn read-surveys []
  (response (read-edn)))

(defn create-survey
  [request]
  (let [request-fp (assoc (:params request) :votes [])
        conj-result (conj (read-edn) request-fp)]
    (write-edn conj-result))
  (response "add module successful"))

(defn remove-survey
  [request]
  (let [request-fp (:params request)
        name-to-remove (:name request-fp)
        file (read-edn)
        new-file (remove (fn [module]
                           (= name-to-remove (:name module))) file)]
    (write-edn (pr-str new-file))
    (response "remove successful")))

(defn update-survey
  [request]
  (let [request-fp (:params request)
        file-data (vec (read-edn))
        module-index (first (remove nil? (map-indexed (fn [i {:keys [name]}]
                                                        (if (= (:name request-fp) name)
                                                          i)) file-data)))
        vote-data (get-in file-data [module-index :votes])
        vote-index (first (remove nil? (map-indexed (fn [i {:keys [id]}]
                                                      (if (= (:id request-fp) id)
                                                        i)) vote-data)))
        new-data (if-not (nil? vote-index)
                   ;;if vote index is not nil, then update-in conj :votes with new request
                   (assoc-in file-data [module-index :votes vote-index] request-fp)
                   (update-in file-data [module-index :votes] conj request-fp))]
    (write-edn (pr-str new-data)))
  (response "update successful"))
