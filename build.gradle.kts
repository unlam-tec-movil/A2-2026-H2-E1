// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.daggerHiltAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ktlint) apply false
}

val externalBuildDir = File(System.getProperty("user.home"), ".android_builds/A2-2026-H2-E1")
allprojects {
    if (System.getenv("CI") != null) return@allprojects
    val subPath = if (path == ":") "root" else path.removePrefix(":").replace(":", "/")
    layout.buildDirectory.set(File(externalBuildDir, subPath))
}
