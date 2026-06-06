package com.example.tugasakhirpam.data

import com.example.tugasakhirpam.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    /*
     * object digunakan agar Supabase client cukup dibuat satu kali.
     * Ini mirip singleton sederhana di Kotlin.
     */
    val client = createSupabaseClient(
        supabaseUrl = requireConfigValue(
            value = BuildConfig.SUPABASE_URL,
            name = "SUPABASE_URL"
        ),
        supabaseKey = requireConfigValue(
            value = BuildConfig.SUPABASE_KEY,
            name = "SUPABASE_KEY"
        )
    ) {
        /*
         * install(Auth) digunakan agar aplikasi bisa memakai fitur autentikasi,
         * seperti login, register, logout, dan membaca session user.
         */
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

    private fun requireConfigValue(value: String, name: String): String {
        return value.ifBlank {
            error("$name belum diisi. Tambahkan nilainya di FrontEnd/local.properties.")
        }
    }
}

