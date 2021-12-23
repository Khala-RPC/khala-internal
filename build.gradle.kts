buildscript {}

plugins {
    kotlin("multiplatform") version "1.6.0"
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
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        //maven("https://dl.bintray.com/mipt-npm/dev")
    }

    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "kotlin-multiplatform")
}

subprojects {
    kotlin {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
        }
        js("browser", IR) {
            attributes.attribute(Attribute.of(String::class.java), "browser")
            browser {
                testTask {
                    useKarma {
                        useChromeHeadless()
                        webpackConfig.cssSupport.enabled = true
                    }
                }
            }
            binaries.executable()
        }
        js("node", IR) {
            attributes.attribute(Attribute.of(String::class.java), "node")
            nodejs {
            }
            binaries.executable()
        }
        val hostOs = System.getProperty("os.name")
        val isLinuxX64 = hostOs == "Linux"
        val isMingwX64 = hostOs.startsWith("Windows")
        val isMacOSX64 = hostOs == "Mac OS X"
        val nativeTarget = when {
            isLinuxX64 -> linuxX64()
            isMingwX64 -> mingwX64()
            //isMacOSX64 -> macosX64()
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }
        sourceSets {
            val commonMain by getting {}
            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                }
            }
            val jvmMain by getting {}
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin("test-junit5"))
                    implementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
                    implementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
                    implementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
                }
            }
            val jsCommonMain by creating {
                dependsOn(commonMain)
            }
            val jsCommonTest by creating {
                dependsOn(commonTest)
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
            val browserMain by getting {
                dependsOn(jsCommonMain)
            }
            val browserTest by getting {
                dependsOn(jsCommonTest)
            }
            val nodeMain by getting {
                dependsOn(jsCommonMain)
            }
            val nodeTest by getting {
                dependsOn(jsCommonTest)
            }
            val nativeMain by creating {
                dependsOn(commonMain)
            }
            val nativeTest by creating {
                dependsOn(commonTest)
            }
            if (isMingwX64) {
                val mingwX64Main by getting {
                    dependsOn(nativeMain)
                }
                val mingwX64Test by getting {
                    dependsOn(nativeTest)
                }
            }
            if (isLinuxX64) {
                val linuxX64Main by getting {
                    dependsOn(nativeMain)
                }
                val linuxX64Test by getting {
                    dependsOn(nativeTest)
                }
            }
            /*
            if (isMacOSX64) {
                val macOSX64Main by getting {
                    dependsOn(nativeMain)
                }
                val macOSX64Test by getting {
                    dependsOn(nativeTest)
                }
            }
            */
        }
    }
}

kotlin {
    jvm()
}

val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.js.ExperimentalJsExport"
}