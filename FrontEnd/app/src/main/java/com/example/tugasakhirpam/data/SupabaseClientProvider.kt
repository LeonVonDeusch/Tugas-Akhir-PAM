package com.example.tugasakhirpam.data

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
        supabaseUrl = "https://msslmdqxzxevekqwfukf.supabase.co",
        supabaseKey = "sb_publishable_HfzFlj-Ro0I0j1aIVOQnQw_8XCbhyBI"
    ) {
        /*
         * install(Auth) digunakan agar aplikasi bisa memakai fitur autentikasi,
         * seperti login, register, logout, dan membaca session user.
         */
        install(Auth)

        /*
         * install(Postgrest) untuk operasi database (select, insert, update, delete).
         * install(Storage) untuk upload/download file gambar ke Supabase Storage.
         */
        install(Postgrest)
        install(Storage)
    }
}

