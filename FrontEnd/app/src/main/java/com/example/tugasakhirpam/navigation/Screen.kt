package com.example.tugasakhirpam.navigation

/*
 * Sealed class juga bisa digunakan untuk route navigasi.
 * Tujuannya agar nama route tidak ditulis manual berkali-kali.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")

    // --- Barang Ditemukan (Found Items - Alvin) ---
    object FoundItemList : Screen("found_item_list")
    object FoundItemDetail : Screen("found_item_detail/{id}") {
        fun createRoute(id: String) = "found_item_detail/$id"
    }
    object FoundItemForm : Screen("found_item_form")

    // --- Barang Hilang (Lost Items - Denta) ---
    object LostItemList : Screen("lost_item_list")
    object LostItemForm : Screen("lost_item_form")
    object LostItemDetail : Screen("lost_item_detail/{itemId}") {
        fun createRoute(itemId: String) = "lost_item_detail/$itemId"
    }


    /*
     * Route untuk halaman klaim barang (membawa id barang hilang yang diklaim).
     */
    object Claim : Screen("claim/{lostItemId}") {
        fun createRoute(lostItemId: String) = "claim/$lostItemId"
    }

    // Tambahkan di dalam sealed class Screen
    object Comment : Screen("comment/{itemType}/{itemId}/{userId}") {
        fun createRoute(itemType: String, itemId: String, userId: String) =
            "comment/$itemType/$itemId/$userId"
    }
}