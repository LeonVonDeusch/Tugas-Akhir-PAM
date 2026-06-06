package com.example.tugasakhirpam.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Model komentar yang sekaligus dipakai untuk decode data dari Supabase.
 *
 * Nama properti sengaja dibuat snake_case agar langsung cocok dengan nama kolom
 * di tabel `comments` Supabase (tidak perlu @SerialName).
 *
 * Field `parent` dan `children` ditandai @Transient karena hanya dipakai di sisi
 * aplikasi untuk membangun struktur balasan (tree), bukan kolom di database.
 */
@Serializable
data class Comment(
    val id: String? = null,
    val user_id: String,
    val user_email: String? = null,
    val item_id: String,
    val item_type: String,
    val content: String,
    val parent_id: String? = null,
    val created_at: String? = null,

    @Transient val parent: Comment? = null,
    @Transient var children: MutableList<Comment> = mutableListOf()
)

/**
 * DTO khusus untuk INSERT komentar baru ke Supabase.
 *
 * Dipisah dari Comment agar `id` dan `created_at` tidak ikut dikirim
 * (keduanya di-generate otomatis oleh database).
 */
@Serializable
data class CommentInsert(
    val user_id: String,
    val user_email: String? = null,
    val item_id: String,
    val item_type: String,
    val content: String,
    val parent_id: String? = null
)
