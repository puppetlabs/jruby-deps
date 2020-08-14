(def jruby-version "9.2.13.0")

(defproject puppetlabs/jruby-deps "9.2.13.0-1-SNAPSHOT"
  :description "JRuby dependencies"
  :url "https://github.com/puppetlabs/jruby-deps"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :min-lein-version "2.7.1"

  :pedantic? :abort

  :dependencies [[org.jruby/jruby-core ~jruby-version]
                 [org.jruby/jruby-stdlib ~jruby-version]]

  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/clojars_jenkins_username
                                     :password :env/clojars_jenkins_password
                                     :sign-releases false}]]

  :plugins [[lein-release-4digit-version "0.2.0"]]

  ;; EZbake relies on this being stored top-level in the jar,
  ;; when including this project as an additional uberjar when building
  ;; pe-puppetserver. Lein 2.8.0 stopped adding project.clj at the top level.
  ;; Long term, it might be better to fix ezbake to handle the file's new location
  ;; under `META-INF/leiningen/group/artifact/project.clj`, but since this is the
  ;; only project that relies on that right now, it's simpler to just add it here.
  :resource-paths ["project.clj"]

  ;; For the jruby-deps uberjar builds, we want to exclude a few "common"
  ;; dependencies which we expect to be provided by other jars in the Java
  ;; classpath. We do this in order to make the dependency resolution
  ;; predictable regardless of the order in which the jars are referenced on the
  ;; classpath. Dependencies in the uberjar profile replace those from the base
  ;; project definition in order to exclude the unwanted dependencies from the
  ;; jruby-deps uberjar.
  :profiles {:uberjar {:dependencies
                       [[org.jruby/jruby-core ~jruby-version
                         :exclusions [joda-time]]]}}

  :uberjar-name "jruby-9k.jar"

  ;; NOTE: jruby-stdlib packages some unexpected things inside
  ;; of its jar.  e.g., it puts a pre-built copy of the bouncycastle
  ;; and snakeyaml jars into its META-INF directory.  This is highly
  ;; undesirable for projects that already have dependencies on different
  ;; versions of these jars.  Items below are excluded from the uberjar.
  :uberjar-exclusions  [#"META-INF/jruby.home/lib/ruby/stdlib/org/bouncycastle"
                        #"META-INF/jruby.home/lib/ruby/stdlib/org/yaml/snakeyaml"])
