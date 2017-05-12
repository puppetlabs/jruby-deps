(def jruby-version "1.7.26")
(def jffi-version "1.2.12")

(def base-dependencies
  "Base set of dependencies which are built into the project's artifact.
  Note that these dependencies are customized somewhat when building an
  uberjar.  See the `uberjar-dependencies` variable."
  [['org.jruby/jruby-core jruby-version
    :exclusions ['com.github.jnr/jffi
                 'com.github.jnr/jnr-x86asm
                 'org.ow2.asm/asm
                 'org.ow2.asm/asm-analysis
                 'org.ow2.asm/asm-commons
                 'org.ow2.asm/asm-tree
                 'org.ow2.asm/asm-util]]

   ;; jruby-core has dependencies on discrete org.ow2.asm artifacts whereas
   ;; other common Clojure projects like core.async declare a dependency on
   ;; org.ow2.asm/asm-all, which provides a superset of the content of the
   ;; discrete org.ow2.asm dependencies.  Defining asm-all here allows for
   ;; conflict resolution to be possible in consuming projects.
   ['org.ow2.asm/asm-all "5.0.3"]

   ;; jffi and jnr-x86asm are explicit dependencies because, in JRuby's poms,
   ;; they are defined using version ranges, and :pedantic? :abort won't
   ;; tolerate this.
   ['com.github.jnr/jffi jffi-version]
   ['com.github.jnr/jffi jffi-version :classifier "native"]
   ['com.github.jnr/jnr-x86asm "1.0.2"]
   ['org.jruby/jruby-stdlib jruby-version]])

;; For the jruby-deps uberjar builds, we want to exclude a few dependencies
;; which we expect to be provided by other jars in the Java classpath.  We
;; do this in order to make the dependency resolution predictable regardless
;; of the order in which the jars are referenced on the classpath.  The
;; following variables list the dependencies which are excluded from the
;; default ones listed in the `base-dependencies` variable above.

(def extra-top-level-dependency-exclusions-from-uberjar
  "Set of dependencies which are excluded from the top-level ones in
  `base-dependencies` when building an uberjar.  For example, if
  `base-dependencies` were to include [['a] ['b] ['c] ['d]] and the set returned
  from this variable were #{'b 'c}, the top-level dependencies built into the
  uberjar would be [['a] ['d]]."
  #{'org.ow2.asm/asm-all})

(def extra-jruby-core-exclusions-from-uberjar
  "Set of dependencies which are added to exclusions from jruby-core in
  `base-dependencies` when building an uberjar.  For example, if the jruby-core
  dependency were excluding ['a 'b] in `base-dependencies` and the set
  returned from this variable were #{'c 'd}, the complete list of dependencies
  which would be excluded from the jruby-core dependency would be
  ['a 'b 'c 'd]."
  #{'joda-time
    'org.yaml/snakeyaml})

(def uberjar-dependencies
  "Customize the list of dependencies from `base-dependencies` for use in
  building an uberjar."
  (->> base-dependencies
    (remove #(contains? extra-top-level-dependency-exclusions-from-uberjar
               (first %)))
    (map #(if (= 'org.jruby/jruby-core (first %))
            (update % (dec (count %))
              (fn [exclusions]
                (concat exclusions extra-jruby-core-exclusions-from-uberjar)))
            (identity %)))))

(defproject puppetlabs/jruby-deps "1.7.26-2-SNAPSHOT"
  :description "JRuby dependencies"
  :url "https://github.com/puppetlabs/jruby-deps"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :min-lein-version "2.7.1"

  :pedantic? :abort

  :dependencies ~base-dependencies

  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/clojars_jenkins_username
                                     :password :env/clojars_jenkins_password
                                     :sign-releases false}]]

  :profiles {:uberjar {:dependencies ~(with-meta
                                        uberjar-dependencies
                                        {:replace true})}}

  :uberjar-name "jruby-1_7.jar"

  ;; NOTE: jruby-stdlib packages some unexpected things inside
  ;; of its jar.  e.g., it puts a pre-built copy of the bouncycastle
  ;; jar into its META-INF directory.  This is highly undesirable
  ;; for projects that already have a dependency on a different
  ;; version of bouncycastle.  Items below are excluded from the uberjar.
  :uberjar-exclusions [#"META-INF/jruby.home/lib/ruby/shared/org/bouncycastle"])
