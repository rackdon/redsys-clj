# redsys-clj

A Clojure library designed to sign redsys requests.

## Latest Version

[![Clojars Project](https://img.shields.io/clojars/v/redsys-clj.svg)](https://clojars.org/redsys-clj)

## Example

Create form data

```clojure
(def merchant-parameters
  {:ds-merchant-merchantcode 999008881
   :ds-merchant-terminal 001
   :ds-merchant-transactiontype 0
   :ds-merchant-amount 100
   :ds-merchant-currency 978
   :ds-merchant-order 1446068581
   :ds-merchant-merchanturl "http://www.prueba.com/urlNotificacion.php"
   :ds-merchant-merchantdata "my optional data"
   :ds-merchant-urlok "http://www.prueba.com/urlOK.php"
   :ds-merchant-urlko "http://www.prueba.com/urlKO.php"})
   
(def commerce-key "Mk9m98IfEblmPrfpsawt7BmxObt98Jev")

(def signature-version "HMAC_SHA256_V1")
   
(defn form-data []
  {:DS_SIGNATUREVERSION signature-version
   :DS_MERCHANTPARAMETERS (rcreate-merchant-parameters merchant-parameters)
   :DS_SIGNATURE (create-merchant-signature merchant-parameters commerce-key)})
```

Manage response

```clojure
(def commerce-key "Mk9m98IfEblmPrfpsawt7BmxObt98Jev")

(defn manage-response [form-params]
  (let [{:keys [DS_SIGNATURE DS_MERCHANT_PARAMETERS]} form-params
        calculated-signature(create-merchant-signature-notif DS_MERCHANT_PARAMETERS commerce-key) 
        decoded-parameters (decode-merchant-parameters DS_MERCHANT_PARAMETERS)]
        
        (if (= calculated-signature DS_SIGNATURE)
          ;; My awesome code
        )))
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
