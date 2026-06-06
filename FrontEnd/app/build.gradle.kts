import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Plugin serialization dibutuhkan oleh Supabase Kotlin
    kotlin("plugin.serialization") version "2.0.21"
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun localProperty(name: String): String {
    return localProperties.getProperty(name)
        ?: providers.gradleProperty(name).orNull
        ?: ""
}

fun buildConfigString(value: String): String {
    val escapedValue = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
    return "\"$escapedValue\""
}

android {
    namespace = "com.example.tugasakhirpam"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.tugasakhirpam"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SUPABASE_URL",
            buildConfigString(localProperty("SUPABASE_URL"))
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY",
            buildConfigString(localProperty("SUPABASE_KEY"))
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Runtime Compose agar bisa collect state dengan lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Supabase BOM
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))

    // Supabase Auth
    implementation("io.github.jan-tennert.supabase:auth-kt")

    // Ktor Android Client
    implementation("io.ktor:ktor-client-android:3.0.3")

    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    implementation("io.github.jan-tennert.supabase:storage-kt")

    implementation("io.coil-kt:coil-compose:2.6.0")
}

