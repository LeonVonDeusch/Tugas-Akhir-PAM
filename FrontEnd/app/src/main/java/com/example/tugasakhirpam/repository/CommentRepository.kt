package com.example.tugasakhirpam.repository

import com.example.tugasakhirpam.data.SupabaseClientProvider
import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.model.CommentInsert
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

/**
 * Repository untuk semua operasi komentar.
 *
 * Versi ini memakai Supabase (Postgrest) langsung, menggantikan Retrofit/REST API
 * lama. Dengan begitu fitur komentar tidak lagi butuh server backend terpisah —
 * cukup tabel `comments` di Supabase.
 *
 * Bertanggung jawab HANYA untuk komunikasi data. Tidak ada logika UI di sini.
 */
class CommentRepository {

    private val supabase = SupabaseClientProvider.client

    /**
     * Mengambil semua komentar untuk satu barang, diurutkan dari yang terlama.
     * @return List<Comment> data mentah (flat list) dari Supabase.
     */
    suspend fun getComments(itemType: String, itemId: String): List<Comment> {
        return supabase.from("comments")
            .select {
                filter {
                    eq("item_type", itemType)
                    eq("item_id", itemId)
                }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<Comment>()
    }

    /**
     * Membuat komentar baru (termasuk reply jika parent_id diisi).
     * Email pembuat ikut disimpan agar bisa ditampilkan seperti komentar YouTube.
     * @return Comment yang baru dibuat dari Supabase.
     */
    suspend fun createComment(comment: Comment): Comment {
        val insert = CommentInsert(
            user_id = comment.user_id,
            user_email = comment.user_email,
            item_id = comment.item_id,
            item_type = comment.item_type,
            content = comment.content,
            parent_id = comment.parent_id
        )

        return supabase.from("comments")
            .insert(insert) { select() }
            .decodeSingle<Comment>()
    }

    /**
     * Mengupdate isi komentar berdasarkan ID.
     */
    suspend fun updateComment(id: String, content: String): Comment {
        return supabase.from("comments")
            .update({ set("content", content) }) {
                filter { eq("id", id) }
                select()
            }
            .decodeSingle<Comment>()
    }

    /**
     * Menghapus komentar berdasarkan ID.
     */
    suspend fun deleteComment(id: String) {
        supabase.from("comments")
            .delete {
                filter { eq("id", id) }
            }
    }
}
