// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}
plugins {
    id ("com.android.application") version "8.2.1" apply false
    id ("com.android.library") version "8.2.1" apply false
    id ("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id ("androidx.navigation.safeargs.kotlin") version "2.6.0" apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}