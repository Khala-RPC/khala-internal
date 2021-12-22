buildscript {}

plugins {
    kotlin("multiplatform") version "1.6.0" apply false
    //maven
    `maven-publish`
}

publishing {
    publications {
        /*
        maven {
            pom {
                withXml {
                    val root = asNode()
                    root.appendNode("name", project.name)
                    root.appendNode("description", "Internal implementation of Khala communication protocol")
                    root.appendNode("url", "https://github.com/khala-rpc")
                }
            }
        }
        */
    }
    repositories {
        maven {
            val user = "khala-rpc"
            val repo = "dev"
            val name = "khala-internal"
            url = uri("https://api.bintray.com/maven/$user/$repo/$name/;publish=1;override=1")
            credentials {
                username = properties["bintray_user"].toString()
                password = properties["bintray_api_key"].toString()
            }
        }
    }
}

allprojects {
    group = "khala"
    version = "0.0.1-alpha"

    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
        //maven("https://dl.bintray.com/mipt-npm/dev")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
}

val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.js.ExperimentalJsExport"
}