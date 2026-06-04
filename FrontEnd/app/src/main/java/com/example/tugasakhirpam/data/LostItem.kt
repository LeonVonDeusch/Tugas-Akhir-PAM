package com.example.tugasakhirpam.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LostItem(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("category_id") val categoryId: Int,
    @SerialName("item_name") val itemName: String,
    @SerialName("description") val description: String,
    @SerialName("last_seen_location") val lastSeenLocation: String,
    @SerialName("date_lost") val dateLost: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("status") val status: String = "belum"
)