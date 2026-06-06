package com.example.tugasakhirpam.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * FoundItem digunakan untuk membaca data dari tabel found_items di Supabase.
 * @Serializable wajib ada agar Supabase Kotlin SDK bisa mengkonversi JSON ke data class.
 * @SerialName digunakan karena nama kolom di database memakai snake_case,
 * sedangkan di Kotlin kita memakai camelCase.
 */
@Serializable
data class FoundItem(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("category_id") val categoryId: Int? = null,
    @SerialName("item_name") val itemName: String = "",
    val description: String = "",
    @SerialName("found_location") val foundLocation: String = "",
    @SerialName("date_found") val dateFound: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    val status: String = "belum",
    @SerialName("created_at") val createdAt: String = ""
)

/*
 * FoundItemInsert digunakan saat membuat laporan baru ke Supabase.
 * Tidak menyertakan id dan created_at karena keduanya di-generate otomatis oleh database.
 */
@Serializable
data class FoundItemInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("category_id") val categoryId: Int? = null,
    @SerialName("item_name") val itemName: String,
    val description: String = "",
    @SerialName("found_location") val foundLocation: String,
    @SerialName("date_found") val dateFound: String,
    @SerialName("image_url") val imageUrl: String? = null,
    val status: String = "belum"
)

/*
 * Category digunakan untuk menampilkan pilihan kategori pada form laporan.
 */
@Serializable
data class Category(
    val id: Int = 0,
    val name: String = "",
    val icon: String = ""
)
