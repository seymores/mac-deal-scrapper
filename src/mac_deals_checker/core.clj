(ns mac-deals-checker.core
  (:require [org.httpkit.client :as http]
            [net.cgrand.enlive-html :as enlive]
            [reaver :refer [parse extract-from text attr]]
            [clojure.java.io :refer [as-url]]
            [clojure.string :as string]))

(def deal-url "http://www.apple.com/sg/shop/browse/home/specialdeals/mac")
(def chrome "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.40 Safari/537.11")

(defn- page
  []
  @(http/get deal-url {:as :stream :user-agent chrome}))

(defn- html-resource
  []
  (enlive/html-resource (:body (page))))

(defn- products
  [resource]
  (enlive/select resource [:.product]))

;; (map string/trim (enlive/select r2 [(enlive/attr= :itemprop "price") enlive/content]))

;; (string/join "\n" (map string/trim (filter #(not (string/blank? %))
;;                                  (enlive/select t1 [:.specs enlive/text-node]))))
(defn- specs
  [product]
  (map string/trim (filter #(not (string/blank? %)) (enlive/select product [:.specs enlive/text-node]))))
  ;; (-> product (enlive/select % [:.specs enlive/text-node]) (filter #(not (string/blank? %)) ) #(map string/trim %)))
  ;; (->> (enlive/select product [:.specs enlive/text-node]) (filter #(not (string/blank? %))) #(map string/trim %)))


;; Works using reaver
;; jsoup selector ref http://jsoup.org/apidocs/org/jsoup/select/Selector.html
(defn latest-deals-info
  []
  (-> deal-url slurp parse (extract-from ".product" [:title :spec :price] "td.specs h3" text "td.specs:nth-child(n+2)" text "span.current_price span span" text)))

