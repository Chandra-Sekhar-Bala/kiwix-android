import plugin.KiwixConfigurationPlugin

plugins {
  android
  id("com.github.triplet.play") version Versions.com_github_triplet_play_gradle_plugin
}
plugins.apply(KiwixConfigurationPlugin::class)

apply(from = rootProject.file("jacoco.gradle"))

ext {
  set("versionMajor", 3)
  set("versionMinor", 5)
  set("versionPatch", 0)
}

fun generateVersionName() = "${ext["versionMajor"]}.${ext["versionMinor"]}.${ext["versionPatch"]}"

/*
* max version code: 21-0-0-00-00-00
* our template    : UU-D-A-ZZ-YY-XX
* where:
* X = patch version
* Y = minor version
* Z = major version (+ 20 to distinguish from previous, non semantic, versions of the app)
* A = number representing ABI split
* D = number representing density split
* U = unused
*/

fun generateVersionCode() =
  20 * 10000 +
    ((ext["versionMajor"] as Int) * 10000) +
    ((ext["versionMinor"] as Int) * 100) +
    ext["versionPatch"] as Int

val apkPrefix get() = System.getenv("TAG") ?: "dev"

android {

  defaultConfig {
    base.archivesBaseName = apkPrefix
    resValue("string", "app_name", "Kiwix")
    resValue("string", "app_search_string", "Search Kiwix")
    versionCode = generateVersionCode()
    versionName = generateVersionName()
    manifestPlaceholders["permission"] = "android.permission.MANAGE_EXTERNAL_STORAGE"
  }

  lintOptions {
    isCheckDependencies = true
  }

  buildTypes {
    getByName("debug") {
      multiDexKeepProguard = file("multidex-instrumentation-config.pro")
      buildConfigField("boolean", "KIWIX_ERROR_ACTIVITY", "false")
      buildConfigField("boolean", "IS_PLAYSTORE", "false")
    }

    getByName("release") {
      buildConfigField("boolean", "KIWIX_ERROR_ACTIVITY", "true")
      buildConfigField("boolean", "IS_PLAYSTORE", "false")
      if (properties.containsKey("disableSigning")) {
        signingConfig = null
      }
    }
    create("playStore") {
      initWith(getByName("release"))
      matchingFallbacks = listOf("release")
      buildConfigField("boolean", "IS_PLAYSTORE", "true")
      manifestPlaceholders["permission"] = "android.permission.placeholder"
    }
  }
  bundle {
    language {
      // This is disabled so that the App Bundle does NOT split the APK for each language.
      // We're gonna use the same APK for all languages.
      enableSplit = false
    }
  }
  sourceSets {
    getByName("androidTest") {
      java.srcDirs("$rootDir/core/src/sharedTestFunctions/java")
    }
  }
}

play {
  isEnabled = true
  serviceAccountCredentials = file("../google.json")
  track = "alpha"
  releaseStatus = "draft"
  resolutionStrategy = "fail"
}

dependencies {
  implementation(Libs.squidb)
}
