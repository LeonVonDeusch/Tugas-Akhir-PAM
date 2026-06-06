package com.example.tugasakhirpam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhirpam.data.SupabaseClientProvider
import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.repository.CommentRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk layar komentar.
 *
 * Tanggung jawab ViewModel:
 * - Menyimpan state UI (loading, sukses, error)
 * - Memanggil repository untuk operasi data
 * - Menjalankan logika bisnis (build tree, flatten)
 * - Menyimpan state sementara seperti komentar yang sedang di-reply
 *
 * Yang TIDAK boleh ada di sini:
 * - Context Android
 * - Toast / Dialog
 * - findViewById
 * - Logika tampilan (View)
 */
class CommentViewModel(
    private val repository: CommentRepository = CommentRepository()
) : ViewModel() {

    // ── State utama UI ─────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<CommentUiState>(CommentUiState.Idle)
    val uiState: StateFlow<CommentUiState> = _uiState

    // ── State komentar yang sedang di-reply ────────────────────────────────────

    private val _replyTarget = MutableStateFlow<Comment?>(null)
    val replyTarget: StateFlow<Comment?> = _replyTarget

    // ── Konstanta item (nantinya bisa dikirim via Intent dari Activity lain) ───

    private var itemId: String = ""
    private var itemType: String = ""

    // ── Inisialisasi ───────────────────────────────────────────────────────────

    /**
     * Dipanggil dari Activity saat pertama kali dibuat.
     * Idealnya itemId dan itemType dikirim lewat Intent, bukan di-hardcode.
     */
    fun init(itemType: String, itemId: String) {
        this.itemType = itemType
        this.itemId = itemId
        loadComments()
    }

    // ── Operasi Data ───────────────────────────────────────────────────────────

    /**
     * Memuat daftar komentar dari server, lalu build tree dan flatten.
     */
    fun loadComments() {
        viewModelScope.launch {
            _uiState.value = CommentUiState.Loading
            try {
                val rawList = repository.getComments(itemType, itemId)
                val tree = buildCommentTree(rawList)
                val flat = flattenComments(tree)
                _uiState.value = CommentUiState.Success(flat)
            } catch (e: Exception) {
                _uiState.value = CommentUiState.Error(e.message ?: "Gagal memuat komentar")
            }
        }
    }

    /**
     * Membuat komentar baru. Jika ada replyTarget, otomatis jadi reply.
     * @param userId ID user yang sedang login (idealnya dari AuthViewModel/session).
     * @param content Isi komentar dari input user.
     */
    fun createComment(userId: String, content: String) {
        if (content.isBlank()) {
            _uiState.value = CommentUiState.Error("Komentar wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = CommentUiState.Loading
            try {
                // Ambil identitas user yang sedang login dari sesi Supabase.
                // Email disimpan agar bisa ditampilkan di tiap komentar (gaya YouTube).
                val user = SupabaseClientProvider.client.auth.currentUserOrNull()

                val comment = Comment(
                    user_id = user?.id ?: userId,
                    user_email = user?.email,
                    item_id = itemId,
                    item_type = itemType,
                    content = content,
                    parent_id = _replyTarget.value?.id
                )
                repository.createComment(comment)
                clearReplyTarget()
                _uiState.value = CommentUiState.ActionSuccess("Komentar berhasil dikirim")
                loadComments() // refresh list
            } catch (e: Exception) {
                _uiState.value = CommentUiState.Error(e.message ?: "Gagal mengirim komentar")
            }
        }
    }

    /**
     * Mengupdate isi komentar.
     */
    fun updateComment(id: String, newContent: String) {
        viewModelScope.launch {
            _uiState.value = CommentUiState.Loading
            try {
                repository.updateComment(id, newContent)
                _uiState.value = CommentUiState.ActionSuccess("Komentar berhasil diupdate")
                loadComments()
            } catch (e: Exception) {
                _uiState.value = CommentUiState.Error(e.message ?: "Gagal mengupdate komentar")
            }
        }
    }

    /**
     * Menghapus komentar berdasarkan ID.
     */
    fun deleteComment(id: String) {
        viewModelScope.launch {
            _uiState.value = CommentUiState.Loading
            try {
                repository.deleteComment(id)
                _uiState.value = CommentUiState.ActionSuccess("Komentar dihapus")
                loadComments()
            } catch (e: Exception) {
                _uiState.value = CommentUiState.Error(e.message ?: "Gagal menghapus komentar")
            }
        }
    }

    // ── State Reply ────────────────────────────────────────────────────────────

    /** Dipanggil ketika user menekan tombol "Reply" pada sebuah komentar */
    fun setReplyTarget(comment: Comment) {
        _replyTarget.value = comment
    }

    /** Dipanggil ketika user membatalkan reply */
    fun clearReplyTarget() {
        _replyTarget.value = null
    }

    /** Reset state ke Idle (misal setelah snackbar/toast ditampilkan) */
    fun resetState() {
        _uiState.value = CommentUiState.Idle
    }

    // ── Logika Bisnis: Tree Building ───────────────────────────────────────────
    //
    // Fungsi ini DIPINDAH dari CommentActivity ke sini karena ini adalah
    // logika bisnis (transformasi data), bukan logika tampilan.

    /**
     * Mengubah flat list menjadi struktur pohon berdasarkan parent_id.
     */
    private fun buildCommentTree(flatList: List<Comment>): List<Comment> {
        val commentMap = mutableMapOf<String, Comment>()
        val rootComments = mutableListOf<Comment>()

        flatList.forEach { comment ->
            if (comment.id != null) commentMap[comment.id] = comment
            comment.children = mutableListOf()
        }

        flatList.forEach { comment ->
            if (comment.parent_id == null) {
                rootComments.add(comment)
            } else {
                val parent = commentMap[comment.parent_id]
                if (parent != null) {
                    parent.children.add(comment)
                } else {
                    rootComments.add(comment) // fallback jika parent tidak ditemukan
                }
            }
        }

        return rootComments
    }

    /**
     * Meratakan struktur pohon menjadi list dengan informasi level indentasi.
     */
    private fun flattenComments(
        comments: List<Comment>,
        level: Int = 0
    ): List<Pair<Comment, Int>> {
        val result = mutableListOf<Pair<Comment, Int>>()
        for (comment in comments) {
            result.add(Pair(comment, level))
            if (comment.children.isNotEmpty()) {
                result.addAll(flattenComments(comment.children, level + 1))
            }
        }
        return result
    }
}