package com.example.tugasakhirpam.viewmodel

import com.example.tugasakhirpam.model.Comment

/**
 * Sealed class untuk merepresentasikan semua kemungkinan state UI pada layar komentar.
 * ViewModel hanya akan emit salah satu dari state di bawah ini.
 */
sealed class CommentUiState {

    /** State awal sebelum ada aksi apapun */
    object Idle : CommentUiState()

    /** Sedang memuat data / mengirim request */
    object Loading : CommentUiState()

    /**
     * Data komentar berhasil dimuat.
     * @param comments List komentar yang sudah di-flatten beserta level indentasinya.
     */
    data class Success(val comments: List<Pair<Comment, Int>>) : CommentUiState()

    /**
     * Operasi CRUD (create/update/delete) berhasil.
     * @param message Pesan sukses untuk ditampilkan ke user.
     */
    data class ActionSuccess(val message: String) : CommentUiState()

    /**
     * Terjadi error.
     * @param message Pesan error untuk ditampilkan ke user.
     */
    data class Error(val message: String) : CommentUiState()
}