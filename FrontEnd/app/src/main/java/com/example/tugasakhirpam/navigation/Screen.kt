package com.example.tugasakhirpam.navigation

/*
 * Sealed class juga bisa digunakan untuk route navigasi.
 * Tujuannya agar nama route tidak ditulis manual berkali-kali.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object LostItemList : Screen("lost_item_list")
    object LostItemForm : Screen("lost_item_form")
    object LostItemDetail : Screen("lost_item_detail/{itemId}") {
        fun createRoute(itemId: String) = "lost_item_detail/$itemId"
    }
}

