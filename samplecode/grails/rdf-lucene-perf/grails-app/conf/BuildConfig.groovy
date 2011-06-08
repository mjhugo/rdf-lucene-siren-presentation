grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenRepo 'http://repo.aduna-software.org/maven2/releases/'

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories 
        //mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        runtime 'info.aduna.commons:aduna-commons-lang:2.9.0'
        runtime 'info.aduna.commons:aduna-commons-io:2.10.0'
        runtime 'info.aduna.commons:aduna-commons-text:2.7.0'
        runtime 'info.aduna.commons:aduna-commons-net:2.7.0'
        runtime 'info.aduna.commons:aduna-commons-xml:2.7.0'
        runtime 'org.openrdf.sesame:sesame-runtime:2.4.0'
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime 'org.apache.lucene:lucene-core:3.1.0' 
        runtime 'org.apache.lucene:lucene-regex:3.0.3'


        // runtime 'mysql:mysql-connector-java:5.1.13'
    }
}
