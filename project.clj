(defproject puppetlabs/jruby-deps "1.7.26-2-SNAPSHOT"
  :description "JRuby dependencies"
  :url "https://github.com/puppetlabs/jruby-deps"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :min-lein-version "2.7.1"

  :pedantic? :abort

  :dependencies [[org.jruby/jruby-core "1.7.26"
                  :exclusions  [com.github.jnr/jffi
                                com.github.jnr/jnr-x86asm
                                joda-time
                                org.yaml/snakeyaml]]
                 ;; jffi and jnr-x86asm are explicit dependencies because,
                 ;; in JRuby's poms, they are defined using version ranges,
                 ;; and :pedantic? :abort won't tolerate this.
                 [com.github.jnr/jffi "1.2.12"]
                 [com.github.jnr/jffi "1.2.12" :classifier "native"]
                 [com.github.jnr/jnr-x86asm "1.0.2"]
                 [org.jruby/jruby-stdlib "1.7.26"]]

  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/clojars_jenkins_username
                                     :password :env/clojars_jenkins_password
                                     :sign-releases false}]]

  :uberjar-name "jruby-1_7.jar"

  ;; NOTE: jruby-stdlib packages some unexpected things inside
  ;; of its jar.  e.g., it puts a pre-built copy of the bouncycastle
  ;; jar into its META-INF directory.  This is highly undesirable
  ;; for projects that already have a dependency on a different
  ;; version of bouncycastle.  Therefore, when building uberjars,
  ;; you should take care to exclude the things that you don't want
  ;; in your final jar.  Here is an example of how you could exclude
  ;; that from the final uberjar:
  :uberjar-exclusions  [#"META-INF/jruby.home/lib/ruby/shared/org/bouncycastle"]

  :plugins [[lein-release-4digit-version "0.2.0"]])
