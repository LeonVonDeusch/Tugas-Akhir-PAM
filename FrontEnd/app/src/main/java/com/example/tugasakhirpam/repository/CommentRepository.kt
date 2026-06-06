package com.example.tugasakhirpam.repository

import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.model.CommentResponse
import com.example.tugasakhirpam.model.GeneralResponse
import com.example.tugasakhirpam.model.SingleCommentResponse
import com.example.tugasakhirpam.model.UpdateCommentRequest
import com.example.tugasakhirpam.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Repository untuk semua operasi komentar.
 *
 * Bertanggung jawab HANYA untuk komunikasi data (API call).
 * Tidak ada logika UI, tidak ada Toast, tidak ada context Android di sini.
 *
 * Semua fungsi bersifat suspend agar bisa dipanggil dari coroutine di ViewModel.
 */
class CommentRepository {

    /**
     * Mengambil semua komentar berdasarkan item.
     * @return List<Comment> data mentah dari API (flat list).
     */
    suspend fun getComments(itemType: String, itemId: String): List<Comment> {
        return suspendCoroutine { continuation ->
            RetrofitClient.api.getComments(itemType, itemId)
                .enqueue(object : Callback<CommentResponse> {
                    override fun onResponse(
                        call: Call<CommentResponse>,
                        response: Response<CommentResponse>
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body()?.data ?: emptyList())
                        } else {
                            continuation.resumeWithException(
                                Exception("Gagal mengambil komentar: ${response.code()}")
                            )
                        }
                    }

                    override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
        }
    }

    /**
     * Membuat komentar baru (termasuk reply jika parent_id diisi).
     * @return Comment yang baru dibuat dari server.
     */
    suspend fun createComment(comment: Comment): Comment {
        return suspendCoroutine { continuation ->
            RetrofitClient.api.createComment(comment)
                .enqueue(object : Callback<SingleCommentResponse> {
                    override fun onResponse(
                        call: Call<SingleCommentResponse>,
                        response: Response<SingleCommentResponse>
                    ) {
                        if (response.isSuccessful) {
                            val created = response.body()?.data
                                ?: throw Exception("Respons kosong dari server")
                            continuation.resume(created)
                        } else {
                            continuation.resumeWithException(
                                Exception("Gagal membuat komentar: ${response.code()}")
                            )
                        }
                    }

                    override fun onFailure(call: Call<SingleCommentResponse>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
        }
    }

    /**
     * Mengupdate isi komentar berdasarkan ID.
     */
    suspend fun updateComment(id: String, content: String): Comment {
        return suspendCoroutine { continuation ->
            RetrofitClient.api.updateComment(id, UpdateCommentRequest(content))
                .enqueue(object : Callback<SingleCommentResponse> {
                    override fun onResponse(
                        call: Call<SingleCommentResponse>,
                        response: Response<SingleCommentResponse>
                    ) {
                        if (response.isSuccessful) {
                            val updated = response.body()?.data
                                ?: throw Exception("Respons kosong dari server")
                            continuation.resume(updated)
                        } else {
                            continuation.resumeWithException(
                                Exception("Gagal mengupdate komentar: ${response.code()}")
                            )
                        }
                    }

                    override fun onFailure(call: Call<SingleCommentResponse>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
        }
    }

    /**
     * Menghapus komentar berdasarkan ID.
     */
    suspend fun deleteComment(id: String) {
        return suspendCoroutine { continuation ->
            RetrofitClient.api.deleteComment(id)
                .enqueue(object : Callback<GeneralResponse> {
                    override fun onResponse(
                        call: Call<GeneralResponse>,
                        response: Response<GeneralResponse>
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWithException(
                                Exception("Gagal menghapus komentar: ${response.code()}")
                            )
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
        }
    }
}