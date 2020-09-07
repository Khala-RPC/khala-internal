

plugins {
    kotlin("multiplatform") version "1.4.0"
    maven
    `maven-publish`
}


group = "khala"
version = "0.0.1-alpha"


publishing {
    publications {
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

repositories {
    mavenCentral()
    mavenLocal()
}

val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
            binaries.executable()
        }
        nodejs {
            binaries.executable()
        }
    }

    val hostOs = System.getProperty("os.name")
    val isLinuxX64 = hostOs == "Linux"
    val isMingwX64 = hostOs.startsWith("Windows")
    val isMacOSX64 = hostOs == "Mac OS X"
    val nativeTarget = when {
        isLinuxX64 -> linuxX64()
        isMingwX64 -> mingwX64()
        isMacOSX64 -> macosX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        val main by compilations.getting
        val msgpackc by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/msgpackc.def")
            packageName("khala.internal.cinterop.msgpack")
        }
        val jsonc by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/jsonc.def")
            packageName("khala.internal.cinterop.json")
        }
        val libzmq by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/libzmq.def")
            packageName("khala.internal.cinterop.zmq")
        }
        val libczmq by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/czmq.def")
            packageName("khala.internal.cinterop.czmq")
        }
        binaries {
            staticLib {
                baseName = "khala-internal-static"
            }
            sharedLib {
                baseName = "khala-internal"
            }
        }
    }

    configure(listOf(targets["metadata"], jvm(), js())) {
        mavenPublication {
            val targetPublication = this@mavenPublication
            tasks.withType<AbstractPublishToMaven>()
                .matching { it.publication == targetPublication }
                .all { onlyIf { isLinuxX64 } }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm(name = "@prodatalab/jszmq", version = "0.2.2"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        /*
        val jsNodeMain by getting {
            dependencies {
                implementation(npm(name = "zeromq", version = "5.2.0"))
            }
        }*/
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
        if (isMacOSX64) {
            val macOSX64Main by getting {
                dependsOn(nativeMain)
            }
            val macOSX64Test by getting {
                dependsOn(nativeTest)
            }
        }
    }
}