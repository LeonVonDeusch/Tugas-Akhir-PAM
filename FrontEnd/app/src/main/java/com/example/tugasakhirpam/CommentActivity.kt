package com.example.tugasakhirpam

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasakhirpam.adapter.CommentAdapter
import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.model.CommentResponse
import com.example.tugasakhirpam.model.SingleCommentResponse
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

        adapter = CommentAdapter(comments) { comment ->

            Toast.makeText(this, "Reply clicked", Toast.LENGTH_SHORT).show()

            selectedReplyComment = comment

            layoutReply.visibility = View.VISIBLE

            tvReplyTo.text = "Reply to: ${comment.content}"
        }

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

                        comments.clear()

                        response.body()?.data?.let {
                            comments.addAll(it)
                        }

                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<CommentResponse>, t: Throwable) {

                    Toast.makeText(
                        this@CommentActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
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
            })
    }
}