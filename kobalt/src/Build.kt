import com.beust.kobalt.*
import com.beust.kobalt.plugin.packaging.*
import com.beust.kobalt.plugin.application.*
import com.beust.kobalt.plugin.kotlin.*

val repos = repos()

val p = project {

    name = "calendar-kotlin"
    group = "com.example"
    artifactId = name
    version = "0.1"

    sourceDirectories {
        path("src/main/kotlin")
    }

    sourceDirectoriesTest {
        path("src/test/kotlin")
    }

    dependencies {
        compile("com.squareup.okhttp3:okhttp:3.3.1")
        compile("com.google.api-client:google-api-client:1.20.0")
        compile("com.google.oauth-client:google-oauth-client-jetty:1.20.0")
        compile("com.google.apis:google-api-services-calendar:v3-rev125-1.20.0")
        compile("com.fasterxml.jackson.core:jackson-databind:2.7.1")
        compile("com.fasterxml.jackson.core:jackson-core:2.7.1")
        compile("com.fasterxml.jackson.core:jackson-annotations:2.7.1")
        compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.7.1-2")
    }

    dependenciesTest {
        compile("org.testng:testng:6.9.9")
    }

    assemble {
        jar {
            fatJar = true
            manifest {
                attributes("Main-Class", "com.example.MainKt")
            }
        }
    }

    application {
        mainClass = "com.example.MainKt"
    }
}
