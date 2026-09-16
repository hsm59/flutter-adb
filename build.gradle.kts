plugins {
    // Bump both to latest stable if Gradle sync complains about metadata/platform versions.
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "dev.husainmukadam"
version = "0.1.0"

kotlin { jvmToolchain(21) }

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        jetbrainsIdeInstallers()
        androidStudioInstallers()
    }
}

dependencies {
    intellijPlatform {
        androidStudio(providers.gradleProperty("studioVersion"))
        bundledPlugin("org.jetbrains.android")
    }
}

intellijPlatform {
    buildSearchableOptions = false
    instrumentCode = false
    pluginConfiguration {
        ideaVersion {
            // Match the "Build #AI-XXX" prefix from Help → About.
            sinceBuild = "261"
            untilBuild = provider { null }
        }
    }
}
