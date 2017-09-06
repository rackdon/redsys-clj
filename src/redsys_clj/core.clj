(ns redsys-clj.core
  (:require [redsys-clj.codings :as codings]))

(defn create-merchant-parameters [data]
  (-> (codings/to-json-str data)
      (.getBytes "UTF-8")
      codings/encode-b64-string))

(defn create-merchant-signature [merchant-parameters commerce-key]
  (let [merchant-params (create-merchant-parameters merchant-parameters)
        secret-ko (codings/encrypt-3DES commerce-key (str (:ds-merchant-order merchant-parameters)))]
    (-> (codings/mac256 merchant-params secret-ko)
        codings/encode-b64-string)))

(defn decode-merchant-parameters [data]
  (-> (.getBytes data "UTF-8")
      codings/decode-b64-string
      codings/to-json))

(defn create-merchant-signature-notif [merchant-parameters commerce-key]
  (let [merchant-params (decode-merchant-parameters merchant-parameters)
        secret-ko (codings/encrypt-3DES commerce-key (get merchant-params "Ds_Order"))]
    (-> (codings/mac256 merchant-parameters secret-ko)
        codings/encode-b64-url-safe)))
