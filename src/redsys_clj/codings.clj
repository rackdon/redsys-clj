(ns redsys-clj.codings
  (:require [clojure.data.codec.base64 :as b64]
            [clojure.string :as string]
            [clojure.data.json :as json])
  (:import [javax.crypto.spec DESedeKeySpec IvParameterSpec SecretKeySpec]
           [javax.crypto Cipher Mac]
           (java.io ByteArrayOutputStream)
           (javax.xml.bind DatatypeConverter)))

(defn to-byte-array [s]
  "Convert an string to byte array"
  (DatatypeConverter/parseBase64Binary  s))

(defn to-json-str [data]
  (json/write-str data
                  :key-fn #(-> (name %)
                               .toUpperCase
                               (clojure.string/replace #"-" "_"))
                  :value-fn (fn [k v] (str v))))

(defn to-json [data]
  (json/read-str data))

(defn encode-b64-string [ba]
  "Convert a byte array to a base64 String"
  (String. (b64/encode ba) "UTF-8"))

(defn encode-b64-url-safe [ba]
  "Convert a byte array to a base64 url safe byte array"
  (string/replace (encode-b64-string ba) #"/|\+" {"/" "_" "+" "-"}))

(defn decode-b64-string [ba]
  "Convert a base64 byte array to a utf-8 String"
  (String. (b64/decode ba) "UTF-8"))

(defn mac256 [string-data secret]
  (let [sha256-mac (Mac/getInstance "HmacSHA256")
        secret-key (SecretKeySpec. secret "HmacSHA256")
        byte-data (.getBytes string-data "UTF-8")]
    (.init sha256-mac secret-key)
    (.doFinal sha256-mac byte-data)))

(defn encrypt-3DES [hex-key data]
  (let [des-key-spec (DESedeKeySpec. (to-byte-array hex-key))
        des-key (SecretKeySpec. (.getKey des-key-spec) "DESede")
        des-cipher (Cipher/getInstance "DESede/CBC/NoPadding")
        required-zeros (- 8 (mod (count data) 8))
        final-required-zeros (if (= required-zeros 8) 0 required-zeros)
        byte-stream-array (ByteArrayOutputStream.)]

    (.init des-cipher Cipher/ENCRYPT_MODE des-key (IvParameterSpec.
                                                    (into-array Byte/TYPE (repeat 8 (byte 0)))))
    (.write byte-stream-array (.getBytes data "UTF-8") 0 (count data))
    (dotimes [x final-required-zeros] (.write byte-stream-array 0))
    (.doFinal des-cipher (.toByteArray byte-stream-array))))
