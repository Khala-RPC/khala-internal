kotlin {
    //explicitApi()
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
                baseName = "khala-internal-transport-static"
            }
            sharedLib {
                baseName = "khala-internal-transport"
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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("co.touchlab:stately-concurrency:1.1.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
            }
        }
        val jsCommonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.9")
            }
        }
    }
}
