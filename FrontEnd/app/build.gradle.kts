plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Plugin serialization dibutuhkan oleh Supabase Kotlin
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.example.tugasakhirpam"
    compileSdk = 36 // Diubah agar formatnya standar Android Studio

    defaultConfig {
        applicationId = "com.example.tugasakhirpam"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    // Supabase Database (Postgrest) -> INI YANG BARU KITA TAMBAHKAN
    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    // Ktor Android Client
    implementation("io.ktor:ktor-client-android:3.0.3")
}
    // Supabase Postgrest (operasi database)
    implementation("io.github.jan-tennert.supabase:postgrest-kt")

    // Supabase Storage (upload & download gambar)
    implementation("io.github.jan-tennert.supabase:storage-kt")

    // Ktor Android Client
    implementation("io.ktor:ktor-client-android:3.0.3")

    // Coil (menampilkan gambar dari URL di Compose)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // --- Dependency dari fitur anggota lain (dari main) ---
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // RecyclerView, Material, CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")
}
