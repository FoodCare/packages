(set-env!
  :wagons '[[s3-wagon-private "1.2.0"]]
  :repositories #(conj % ["cimpatico" {:url        "s3p://cimpatico-maven/releases/"
                                       :username   (System/getenv "AWS_ACCESS_KEY_ID")
                                       :passphrase (System/getenv "AWS_SECRET_ACCESS_KEY")}])
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.8.1" :scope "test"]
                  [cljsjs/create-react-class "15.6.2-0"]
                  [cljsjs/quill "1.2.6-cimpatico-0"]
                  [cljsjs/prop-types "15.5.10-1"]
                  [cljsjs/react "15.5.0-0"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "1.1.0")
(def +version+ (str +lib-version+ "-cimpatico-6"))

(task-options!
  pom {:project     'cljsjs/react-quill
       :version     +version+
       :description "A Quill component for React."
       :url         "http://zenoamaro.github.io/react-quill/"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"MIT" "https://raw.githubusercontent.com/zenoamaro/react-quill/master/LICENSE"}})

(deftask download-react-quill []
         (download :url (str "https://github.com/zenoamaro/react-quill/releases/download/v" +lib-version+ "/react-quill-" +lib-version+ ".tgz")
                   :checksum "1629c6d2cba935575776a9b127b43fcf"
                   :decompress true
                   :archive-format "tar"
                   :compression-format "gz"))

(deftask package []
         (comp
           (download-react-quill)
           (sift :move {#"^package/dist/react-quill.js"
                        "cljsjs/react-quill/development/react-quill.inc.js"
                        #"^package/dist/react-quill.min.js"
                        "cljsjs/react-quill/production/react-quill.min.inc.js"})
           (sift :include #{#"^cljsjs"})
           (deps-cljs :name "cljsjs.react-quill"
                      :requires ["cljsjs.quill" "cljsjs.react"])
           (pom)
           (jar)))
