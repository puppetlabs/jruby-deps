(def jruby-version "1.7.27")
(def jffi-version "1.2.12")

(defn deps-with-jruby-core-exclusions
  "Return a vector of the project's dependency coordinates with any arguments
  supplied being appended as exclusions from the org.jruby/jruby-core
  dependency."
  [& extra-jruby-core-exclusions]
  [['org.jruby/jruby-core jruby-version
    :exclusions (concat ['com.github.jnr/jffi
                         'com.github.jnr/jnr-x86asm
                         'org.ow2.asm/asm
                         'org.ow2.asm/asm-analysis
                         'org.ow2.asm/asm-commons
                         'org.ow2.asm/asm-tree
                         'org.ow2.asm/asm-util]
                  extra-jruby-core-exclusions)]
   ;; jffi and jnr-x86asm are explicit dependencies because, in JRuby's poms,
   ;; they are defined using version ranges, and :pedantic? :abort won't
   ;; tolerate this.
   ['com.github.jnr/jffi jffi-version]
   ['com.github.jnr/jffi jffi-version :classifier "native"]
   ['com.github.jnr/jnr-x86asm "1.0.2"]
   ['org.jruby/jruby-stdlib jruby-version]])

(defproject puppetlabs/jruby-deps "1.7.27-1"
  :description "JRuby dependencies"
  :url "https://github.com/puppetlabs/jruby-deps"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :min-lein-version "2.7.1"

  :pedantic? :abort

  :dependencies ~(conj
                   (deps-with-jruby-core-exclusions)
                   ;; jruby-core has dependencies on discrete org.ow2.asm
                   ;; artifacts whereas other common Clojure projects like
                   ;; core.async declare a dependency on org.ow2.asm/asm-all,
                   ;; which provides a superset of the content of the discrete
                   ;; org.ow2.asm dependencies. Defining asm-all here allows
                   ;; for conflict resolution to be possible in consuming
                   ;; projects.
                   ['org.ow2.asm/asm-all "5.0.3"])

  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/clojars_jenkins_username
                                     :password :env/clojars_jenkins_password
                                     :sign-releases false}]]

  ;; For the jruby-deps uberjar builds, we want to exclude a few "common"
  ;; dependencies  which we expect to be provided by other jars in the Java
  ;; classpath. We do this in order to make the dependency resolution
  ;; predictable regardless of the order in which the jars are referenced on the
  ;; classpath. Dependencies in the uberjar profile replace those from the base
  ;; project definition in order to exclude the unwanted dependencies from the
  ;; jruby-deps uberjar.
  :profiles {:uberjar {:dependencies ~(with-meta
                                        (deps-with-jruby-core-exclusions
                                          'joda-time 'org.yaml/snakeyaml)
                                        {:replace true})}}

  :uberjar-name "jruby-1_7.jar"

  ;; NOTE: jruby-stdlib packages some unexpected things inside
  ;; of its jar.  e.g., it puts a pre-built copy of the bouncycastle
  ;; jar into its META-INF directory.  This is highly undesirable
  ;; for projects that already have a dependency on a different
  ;; version of bouncycastle.  Items below are excluded from the uberjar.
  :uberjar-exclusions [#"META-INF/jruby.home/lib/ruby/shared/org/bouncycastle"])
