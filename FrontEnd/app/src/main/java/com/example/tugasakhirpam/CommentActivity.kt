package com.example.tugasakhirpam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasakhirpam.adapter.CommentAdapter
import com.example.tugasakhirpam.adapter.ThreadLineDecoration
//import com.example.tugasakhirpam.adapter.ThreadLineDecoration
import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.viewmodel.CommentUiState
import com.example.tugasakhirpam.viewmodel.CommentViewModel
import kotlinx.coroutines.launch

/**
 * CommentActivity — hanya bertanggung jawab untuk:
 * 1. Menampilkan data yang sudah disiapkan ViewModel
 * 2. Meneruskan aksi user ke ViewModel
 * 3. Menampilkan feedback visual (loading, toast, dialog)
 *
 * Tidak ada lagi: API call, logika bisnis, atau transformasi data di sini.
 */
class CommentActivity : AppCompatActivity() {

    // ── View bindings ──────────────────────────────────────────────────────────

    private lateinit var recyclerView: RecyclerView
    private lateinit var etComment: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnCloseReply: ImageButton
    private lateinit var layoutReply: LinearLayout
    private lateinit var tvReplyTo: TextView

    private lateinit var adapter: CommentAdapter

    // ── ViewModel ──────────────────────────────────────────────────────────────

    // viewModels() adalah delegate dari Activity KTX — otomatis menangani lifecycle
    private val viewModel: CommentViewModel by viewModels()

    // ── Data item (dikirim lewat Intent dari halaman detail) ───────────────────
    // Nilai default tetap dipakai sebagai fallback jika Activity dibuka tanpa Intent extra.

    private var itemId = ""
    private var itemType = ""
    private var currentUserId = ""

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        // Ambil data item dari Intent. Jika tidak ada (mis. dibuka langsung),
        // pakai nilai default agar Activity tetap berfungsi.
        itemType = intent.getStringExtra(EXTRA_ITEM_TYPE) ?: "lost"
        itemId = intent.getStringExtra(EXTRA_ITEM_ID) ?: "660e8400-e29b-41d4-a716-446655440111"
        currentUserId = intent.getStringExtra(EXTRA_USER_ID) ?: "550e8400-e29b-41d4-a716-446655440001"

        bindViews()
        setupAdapter()
        setupListeners()
        observeViewModel()

        // Inisialisasi ViewModel dengan data item
        viewModel.init(itemType, itemId)
    }

    // ── Setup ──────────────────────────────────────────────────────────────────

    private fun bindViews() {
        recyclerView = findViewById(R.id.recyclerComments)
        etComment = findViewById(R.id.etComment)
        btnSend = findViewById(R.id.btnSend)
        btnCloseReply = findViewById(R.id.btnCloseReply)
        layoutReply = findViewById(R.id.layoutReply)
        tvReplyTo = findViewById(R.id.tvReplyTo)
    }

    private fun setupAdapter() {
        adapter = CommentAdapter(
            items = emptyList(),
            onReplyClick = { comment ->
                // Teruskan ke ViewModel, bukan diurus di sini
                viewModel.setReplyTarget(comment)
            },
            onEditClick = { comment ->
                showEditDialog(comment)
            },
            onDeleteClick = { comment ->
                comment.id?.let { viewModel.deleteComment(it) }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ThreadLineDecoration(this))
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        btnSend.setOnClickListener {
            val content = etComment.text.toString()
            viewModel.createComment(currentUserId, content)
            etComment.text.clear()
        }

        btnCloseReply.setOnClickListener {
            viewModel.clearReplyTarget()
        }
    }

    // ── Observasi State dari ViewModel ─────────────────────────────────────────

    private fun observeViewModel() {
        lifecycleScope.launch {
            // repeatOnLifecycle memastikan collect berhenti saat Activity di-pause
            // dan resume lagi saat Activity kembali aktif — lebih aman dari lifecycleScope biasa
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observasi state utama UI
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }

                // Observasi target reply secara terpisah
                launch {
                    viewModel.replyTarget.collect { replyComment ->
                        updateReplyBanner(replyComment)
                    }
                }
            }
        }
    }

    /**
     * Semua perubahan state UI ditangani di satu tempat.
     * Activity tidak perlu tahu bagaimana data diproses — cukup tampilkan hasilnya.
     */
    private fun handleUiState(state: CommentUiState) {
        when (state) {
            is CommentUiState.Idle -> {
                // Tidak ada aksi
            }

            is CommentUiState.Loading -> {
                // TODO: Tampilkan ProgressBar jika ada di layout
                // progressBar.visibility = View.VISIBLE
            }

            is CommentUiState.Success -> {
                // progressBar.visibility = View.GONE
                adapter.updateComments(state.comments)
            }

            is CommentUiState.ActionSuccess -> {
                // progressBar.visibility = View.GONE
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }

            is CommentUiState.Error -> {
                // progressBar.visibility = View.GONE
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    /**
     * Menampilkan atau menyembunyikan banner "Reply to: ..." berdasarkan state.
     */
    private fun updateReplyBanner(replyComment: Comment?) {
        if (replyComment != null) {
            layoutReply.visibility = View.VISIBLE
            tvReplyTo.text = "Reply to: ${replyComment.content}"
        } else {
            layoutReply.visibility = View.GONE
            tvReplyTo.text = ""
        }
    }

    // ── Dialog ─────────────────────────────────────────────────────────────────

    /**
     * Dialog edit komentar.
     * Activity boleh menampilkan dialog karena itu bagian dari UI,
     * tapi aksi "update" tetap diteruskan ke ViewModel.
     */
    private fun showEditDialog(comment: Comment) {
        val editText = EditText(this).apply {
            setText(comment.content)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Komentar")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newContent = editText.text.toString()
                comment.id?.let { viewModel.updateComment(it, newContent) }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ── Helper pembuat Intent ────────────────────────────────────────────────────

    companion object {
        const val EXTRA_ITEM_TYPE = "extra_item_type"
        const val EXTRA_ITEM_ID = "extra_item_id"
        const val EXTRA_USER_ID = "extra_user_id"

        /**
         * Membuat Intent untuk membuka layar komentar suatu barang.
         * @param itemType jenis barang: "lost" atau "found"
         * @param itemId   ID barang yang komentarnya ingin dilihat
         * @param userId   ID user yang sedang login (pembuat komentar)
         */
        fun newIntent(context: Context, itemType: String, itemId: String, userId: String): Intent {
            return Intent(context, CommentActivity::class.java).apply {
                putExtra(EXTRA_ITEM_TYPE, itemType)
                putExtra(EXTRA_ITEM_ID, itemId)
                putExtra(EXTRA_USER_ID, userId)
            }
        }
    }
}