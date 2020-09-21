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
    jcenter()
    maven("https://dl.bintray.com/mipt-npm/dev")
}

val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.js.ExperimentalJsExport"
}

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
        val czmq by main.cinterops.creating {
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
                implementation("org.jetbrains.kotlinx:kotlinx-io:0.2.0-npm-dev-11")
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
