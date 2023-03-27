plugins {
    kotlin("multiplatform") version "1.7.21"
    id("maven-publish")
}

group = "site.j2k"
version = "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    if (!hostOs.startsWith("Windows")) {
        throw GradleException("Host OS is not supported")
    }

    mingwX64("native").apply {
        binaries {
            sharedLib {
                baseName = "winui"
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}

publishing {
    repositories {
        maven {
            url = uri("C:\\Users\\kseme\\.m2\\repository")
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifact("$buildDir/bin/native/releaseShared/libWinUI.dll") {
                extension = "dll"
//                builtBy("linkWinUISharedLib")
            }
        }
    }
}
