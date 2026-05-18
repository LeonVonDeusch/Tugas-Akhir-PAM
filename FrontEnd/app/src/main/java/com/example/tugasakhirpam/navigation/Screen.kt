package com.example.tugasakhirpam.navigation

/*
 * Sealed class juga bisa digunakan untuk route navigasi.
 * Tujuannya agar nama route tidak ditulis manual berkali-kali.
 */
sealed class Screen(val route: String) {

    /*
     * Route untuk halaman login.
     */
    object Login : Screen("login")

    /*
     * Route untuk halaman register.
     */
    object Register : Screen("register")

    /*
     * Route untuk halaman dashboard.
     */
    object Dashboard : Screen("dashboard")

    /*
     * Route untuk daftar semua barang ditemukan.
     */
    object FoundItemList : Screen("found_item_list")

    /*
     * Route untuk detail satu barang ditemukan.
     * {id} adalah parameter ID barang yang diklik.
     */
    object FoundItemDetail : Screen("found_item_detail/{id}") {
        fun createRoute(id: String) = "found_item_detail/$id"
    }

    /*
     * Route untuk form lapor barang ditemukan.
     */
    object FoundItemForm : Screen("found_item_form")
}

