package com.example.tugasakhirpam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasakhirpam.R
import com.example.tugasakhirpam.model.Comment

class CommentAdapter(
    private val items: List<Pair<Comment, Int>>,
    private val onReplyClick: (Comment) -> Unit,
    private val onEditClick: (Comment) -> Unit,
    private val onDeleteClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    companion object {
        const val TYPE_COMMENT = 0
        const val TYPE_REPLY = 1
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val btnReply: ImageButton = view.findViewById(R.id.btnReply)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val tvReplyTo: TextView? = view.findViewById(R.id.tvReplyTo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == TYPE_COMMENT) {
            R.layout.item_comment
        } else {
            R.layout.item_reply_comment
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (comment, level) = items[position]

        holder.tvContent.text = comment.content
        holder.tvDate.text = comment.created_at ?: ""

        holder.btnReply.setOnClickListener { onReplyClick(comment) }
        holder.btnEdit.setOnClickListener { onEditClick(comment) }
        holder.btnDelete.setOnClickListener { onDeleteClick(comment) }

        // Tampilkan "Reply to" hanya di item reply
        if (comment.parent != null) {
            holder.tvReplyTo?.visibility = View.VISIBLE
            holder.tvReplyTo?.text = "Reply to: ${comment.parent.content}"
        } else {
            holder.tvReplyTo?.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        // ✅ Fix: akses .first karena items bertipe List<Pair<Comment, Int>>
        return if (items[position].first.parent_id == null) {
            TYPE_COMMENT
        } else {
            TYPE_REPLY
        }
    }
}