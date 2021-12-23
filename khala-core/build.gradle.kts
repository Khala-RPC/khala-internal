kotlin {
    //explicitApi()
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
    }
}
