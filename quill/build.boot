(set-env!
  :wagons '[[s3-wagon-private "1.2.0"]]
  :repositories #(conj % ["cimpatico" {:url        "s3p://cimpatico-maven/releases/"
                                       :username   (System/getenv "AWS_ACCESS_KEY_ID")
                                       :passphrase (System/getenv "AWS_SECRET_ACCESS_KEY")}])
  :resource-paths #{"resources"}
  :dependencies '[[cljsjs/boot-cljsjs "0.8.1" :scope "test"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "1.2.6") ;; released at May 28, 2017
(def +version+ (str +lib-version+ "-cimpatico-0"))

(task-options!
  pom {:project     'cljsjs/quill
       :version     +version+
       :description "Quill is a free, open source WYSIWYG editor built for the modern web."
       :url         "http://quilljs.com/"
       :license     {"BSD 3-Clause" "http://opensource.org/licenses/BSD-3-Clause"}})

(deftask package []
         (comp
           (download :url
                     (format "https://github.com/quilljs/quill/releases/download/v%s/quill.tar.gz" +lib-version+)
                     :checksum "2AE893E0D29AF3569FB38CCDDFFEBDD8"
                     :decompress true
                     :archive-format "tar"
                     :compression-format "gz")
           (sift :move {#".*quill\.js"        "cljsjs/quill/quill.inc.js"
                        #".*quill\.min\.js"   "cljsjs/quill/quill.min.inc.js"
                        #".*quill\.core\.css" "cljsjs/quill/quill.core.css"
                        #".*dist/quill\.snow\.css" "cljsjs/quill/quill.snow.css"})
           (sift :include #{#"^cljsjs"})
           (deps-cljs :name "cljsjs.quill")
           (pom)
           (jar)))
