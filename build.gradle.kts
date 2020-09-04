plugins {
    kotlin("multiplatform") version "1.4.0"
    id("maven-publish")
}
group = "kscience.khala"
version = "1.0-SNAPSHOT"

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
    // Common code between browser and node.js
    js("jsCommon") {
        browser()
        nodejs()
    }
    js("jsBrowser") {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
            binaries.executable()
        }

    }
    js("jsNode") {
        nodejs {
            binaries.executable()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        val main by compilations.getting
        val msgpackc by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/msgpackc.def")
            packageName("kscience.khala.internal.cinterop.msgpack")
        }
        val jsonc by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/jsonc.def")
            packageName("kscience.khala.internal.cinterop.json")
        }
        val libzmq by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/libzmq.def")
            packageName("kscience.khala.internal.cinterop.zmq")
        }
        val libczmq by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/libczmq.def")
            packageName("kscience.khala.internal.cinterop.czmq")
        }
        binaries {
            executable {
                entryPoint = "kscience.khala.internal.main"
            }
            /*
            staticLib {
                baseName = "khala-internal-static"
            }
            sharedLib {
                baseName = "khala-internal"
            }
            */
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
        val jsCommonMain by getting
        val jsCommonTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val jsBrowserMain by getting {
            dependsOn(jsCommonMain)
            dependencies {
                implementation(npm(name = "@prodatalab/jszmq", version = "0.2.2"))
            }
        }
        val jsBrowserTest by getting {
            dependsOn(jsCommonTest)
        }
        val jsNodeMain by getting {
            dependsOn(jsCommonMain)
            dependencies {
                implementation(npm(name = "zeromq", version = "5.2.0"))
            }
        }
        val jsNodeTest by getting {
            dependsOn(jsCommonTest)
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}