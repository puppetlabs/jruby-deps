(def jruby-version "9.4.1.0")

(defproject puppetlabs/jruby-deps "9.4.1.0-1-SNAPSHOT"
  :description "JRuby dependencies"
  :url "https://github.com/puppetlabs/jruby-deps"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :min-lein-version "2.7.1"

  :pedantic? :abort

  :dependencies [[com.github.jnr/jnr-enxio "0.32.13"]
                 [com.github.jnr/jnr-unixsocket "0.38.17"]
                 [com.github.jnr/jnr-posix "3.1.15"]
                 [com.github.jnr/jnr-constants "0.10.3"]
                 [com.github.jnr/jnr-ffi "2.2.11"]
                 [org.jruby/jruby-base ~jruby-version
                  :exclusions [com.github.jnr/jffi com.github.jnr/jnr-enxio com.github.jnr/jnr-unixsocket com.github.jnr/jnr-posix com.github.jnr/jnr-constants com.github.jnr/jnr-ffi joda-time]]
                 [org.jruby/jruby-stdlib ~jruby-version]
                 [org.snakeyaml/snakeyaml-engine "2.6"]
                 [joda-time "2.10.10"]]

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

  :uberjar-name "jruby-9k.jar")
