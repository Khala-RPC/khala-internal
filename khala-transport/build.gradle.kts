kotlin {
    //explicitApi()
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

    nativeTarget.apply {
        val main by compilations.getting
        val msgpackc by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/msgpackc.def")
            packageName("khala.internal.transport.cinterop.msgpack")
        }
        val jsonc by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/jsonc.def")
            packageName("khala.internal.transport.cinterop.json")
        }
        val libzmq by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/libzmq.def")
            packageName("khala.internal.transport.cinterop.zmq")
        }
        val czmq by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/czmq.def")
            packageName("khala.internal.transport.cinterop.czmq")
        }
        val nghttp2 by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/nghttp2.def")
            packageName("khala.internal.transport.cinterop.nghttp2")
        }
        val openssl by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/openssl.def")
            packageName("khala.internal.transport.cinterop.openssl")
        }
        val libevent by main.cinterops.creating {
            defFile("src/nativeInterop/cinterop/libevent.def")
            packageName("khala.internal.transport.cinterop.libevent")
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

    configure(listOf(targets["metadata"], jvm(), js("browser"), js("node"))) {
        mavenPublication {
            val targetPublication = this@mavenPublication
            tasks.withType<AbstractPublishToMaven>()
                .matching { it.publication == targetPublication }
                .all { onlyIf { isLinuxX64 } }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("co.touchlab:stately-isolate:1.1.1-a1")
                implementation("io.github.microutils:kotlin-logging:2.1.0")
                //implementation("org.jetbrains.kotlinx:kotlinx-io:0.2.0-npm-dev-11")
                //implementation("org.jetbrains.kotlinx:kotlinx-io:0.1.16")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("co.touchlab:stately-concurrency:1.1.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.zeromq:jeromq:0.5.2")
                implementation("org.json:json:20200518")
            }
        }
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.9")
            }
        }
        val browserMain by getting {
            dependsOn(jsCommonMain)
            dependencies {
                implementation(npm(name = "@prodatalab/jszmq", version = "0.2.2"))
            }
        }
        val browserTest by getting {
            dependsOn(jsCommonTest)
        }
        val nodeMain by getting {
            dependsOn(jsCommonMain)
            dependencies {
                implementation(npm(name = "zeromq", version = "5.2.0"))
                implementation(npm(name = "@prodatalab/jszmq", version = "0.2.2"))
            }
        }
        val nodeTest by getting {
            dependsOn(jsCommonTest)
            dependencies {
                implementation(kotlin("test-js"))
            }
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
