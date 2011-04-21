(ns pallet.crate.etc-default
  "Generation and installation of /etc/default-style files."
 (:require
   [pallet.stevedore :as stevedore]
   [pallet.action.file :as file]
   [pallet.action.remote-file :as remote-file]
   [pallet.script.lib :as lib]
   [clojure.string :as string]))

(defn write
  "Writes a KEY=value file to /etc/default/~{filename}, or ~{filename} if
   filename starts with a /.  Note that all values are quoted, and quotes in
   values are escaped, but otherwise, values are written literally.

   e.g. (write \"tomcat6\"
          :JAVA_OPTS \"-Xmx1024m\"
          \"JSP_COMPILER\" \"javac\")"
  [session filename & key-value-pairs]
  (let [file (if (= \/ (first filename))
               filename
               (str (stevedore/script (~lib/etc-default)) "/" filename))]
    (-> session
        (remote-file/remote-file
         file
         :owner "root:root"
         :mode 644
         :content (string/join
                   \newline
                   (for [[k v] (partition 2 key-value-pairs)]
                     (str (name k) "=" (pr-str v))))))))
