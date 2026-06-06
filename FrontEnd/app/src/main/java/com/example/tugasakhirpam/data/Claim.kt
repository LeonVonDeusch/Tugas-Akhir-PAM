package com.example.tugasakhirpam.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO untuk menyimpan klaim kepemilikan atas sebuah barang hilang ke Supabase
 * (tabel "claims"). Terhubung ke lost_items lewat foreign key lost_item_id.
 */
@Serializable
data class ClaimInsert(
    @SerialName("lost_item_id") val lostItemId: String,
    @SerialName("claimer_id") val claimerId: String,
    @SerialName("proof_description") val proofDescription: String,
    @SerialName("contact_info") val contactInfo: String,
    @SerialName("message") val message: String? = null,
    @SerialName("status") val status: String = "pending"
)
