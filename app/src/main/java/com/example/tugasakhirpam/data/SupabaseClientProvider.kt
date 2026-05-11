package com.example.tugasakhirpam.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

object SupabaseClientProvider {

    /*
     * object digunakan agar Supabase client cukup dibuat satu kali.
     * Ini mirip singleton sederhana di Kotlin.
     */
    val client = createSupabaseClient(
        supabaseUrl = "Project_URL",
        supabaseKey = "Publishable key"
    ) {
        /*
         * install(Auth) digunakan agar aplikasi bisa memakai fitur autentikasi,
         * seperti login, register, logout, dan membaca session user.
         */
        install(Auth)
    }
}

