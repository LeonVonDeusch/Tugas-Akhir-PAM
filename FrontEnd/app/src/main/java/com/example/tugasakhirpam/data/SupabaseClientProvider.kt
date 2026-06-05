package com.example.tugasakhirpam.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

    val client = createSupabaseClient(
        supabaseUrl = "https://msslmdqxzxevekqwfukf.supabase.co",
        supabaseKey = "sb_publishable_HfzFlj-Ro0I0j1aIVOQnQw_8XCbhyBI"
    ) {
        install(Auth)
        install(Postgrest)
    }
}