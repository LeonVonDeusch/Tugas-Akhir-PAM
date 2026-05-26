package com.example.tugasakhirpam

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasakhirpam.adapter.CommentAdapter
import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.model.CommentResponse
import com.example.tugasakhirpam.model.GeneralResponse
import com.example.tugasakhirpam.model.SingleCommentResponse
import com.example.tugasakhirpam.model.UpdateCommentRequest
import com.example.tugasakhirpam.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etComment: EditText
    private lateinit var btnSend: ImageButton

    private lateinit var btnCloseReply: ImageButton
    private val comments = mutableListOf<Comment>()

    private lateinit var adapter: CommentAdapter

    private var selectedReplyComment: Comment? = null

    private lateinit var layoutReply: LinearLayout

    private lateinit var tvReplyTo: TextView

    // contoh item
    private val itemId = "660e8400-e29b-41d4-a716-446655440111"
    private val itemType = "lost"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        recyclerView = findViewById(R.id.recyclerComments)
        etComment = findViewById(R.id.etComment)
        btnSend = findViewById(R.id.btnSend)
        btnCloseReply = findViewById(R.id.btnCloseReply)

        layoutReply = findViewById(R.id.layoutReply)

        tvReplyTo = findViewById(R.id.tvReplyTo)

        adapter = CommentAdapter(
            emptyList(),
            onReplyClick = { comment ->
                selectedReplyComment = comment
                layoutReply.visibility = View.VISIBLE
                tvReplyTo.text = "Reply to: ${comment.content}"
            },
            onEditClick = { comment -> showEditDialog(comment) },
            onDeleteClick = { comment -> deleteComment(comment.id!!) }
        )


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        getComments()

        btnSend.setOnClickListener {
            createComment()
        }
    }

    private fun getComments() {

        RetrofitClient.api.getComments(itemType, itemId)
            .enqueue(object : Callback<CommentResponse> {

                override fun onResponse(
                    call: Call<CommentResponse>,
                    response: Response<CommentResponse>
                ) {

                    if (response.isSuccessful) {

                        val responseData = response.body()?.data ?: emptyList()

                        val tree = buildCommentTree(responseData)
                        val flat = flattenComments(tree)

                        adapter = CommentAdapter(
                            flat, // 🔥 PAKAI FLATTEN
                            onReplyClick = { comment ->
                                selectedReplyComment = comment
                                layoutReply.visibility = View.VISIBLE
                                tvReplyTo.text = "Reply to: ${comment.content}"
                            },
                            onEditClick = { comment ->
                                showEditDialog(comment)
                            },
                            onDeleteClick = { comment ->
                                deleteComment(comment.id!!)
                            }
                        )

                        recyclerView.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                    Toast.makeText(this@CommentActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun createComment() {

        val content = etComment.text.toString()

        if (content.isEmpty()) {
            etComment.error = "Komentar wajib diisi"
            return
        }

        val comment = Comment(

            user_id = "550e8400-e29b-41d4-a716-446655440000",

            item_id = itemId,

            item_type = itemType,

            content = content,

            parent_id = selectedReplyComment?.id
        )

        RetrofitClient.api.createComment(comment)
            .enqueue(object : Callback<SingleCommentResponse> {

                override fun onResponse(
                    call: Call<SingleCommentResponse>,
                    response: Response<SingleCommentResponse>
                ) {

                    if (response.isSuccessful) {

                        etComment.text.clear()

                        getComments()

                        selectedReplyComment = null

                        layoutReply.visibility = View.GONE

                        tvReplyTo.text = ""

                        btnCloseReply.setOnClickListener {

                            selectedReplyComment = null

                            layoutReply.visibility = View.GONE
                        }

                        Toast.makeText(
                            this@CommentActivity,
                            "Komentar berhasil dikirim",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<SingleCommentResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@CommentActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    private fun deleteComment(id: String) {

        RetrofitClient.api.deleteComment(id)
            .enqueue(object : Callback<GeneralResponse> {

                override fun onResponse(
                    call: Call<GeneralResponse>,
                    response: Response<GeneralResponse>
                ) {

                    Log.d("DELETE", "Response: ${response.body()}")

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@CommentActivity,
                            "Komentar dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        getComments()
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {

                    Log.e("DELETE", t.message.toString())
                }
            })
    }

    private fun showEditDialog(comment: Comment) {

        val editText = EditText(this)
        editText.setText(comment.content)

        AlertDialog.Builder(this)
            .setTitle("Edit Komentar")
            .setView(editText)

            .setPositiveButton("Update") { _, _ ->

                val newContent = editText.text.toString()

                updateComment(
                    comment.id!!,
                    editText.text.toString()
                )
            }

            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateComment(id: String, content: String) {

        val request = UpdateCommentRequest(content)

        RetrofitClient.api.updateComment(id, request)
            .enqueue(object : Callback<SingleCommentResponse> {

                override fun onResponse(
                    call: Call<SingleCommentResponse>,
                    response: Response<SingleCommentResponse>
                ) {

                    Log.d("UPDATE", "Response: ${response.body()}")

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@CommentActivity,
                            "Update berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        getComments() // refresh data
                    }
                }

                override fun onFailure(call: Call<SingleCommentResponse>, t: Throwable) {

                    Log.e("UPDATE", t.message.toString())

                    Toast.makeText(
                        this@CommentActivity,
                        "Gagal update",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

fun buildCommentTree(flatList: List<Comment>): List<Comment> {

    val commentMap = mutableMapOf<String?, Comment>()
    val rootComments = mutableListOf<Comment>()

    flatList.forEach {
        commentMap[it.id] = it
        it.children = mutableListOf()
    }

    flatList.forEach { comment ->
        if (comment.parent_id == null) {
            rootComments.add(comment)
        } else {
            val parent = commentMap[comment.parent_id]
            parent?.children?.add(comment)
        }
    }

    return rootComments
}

fun flattenComments(
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