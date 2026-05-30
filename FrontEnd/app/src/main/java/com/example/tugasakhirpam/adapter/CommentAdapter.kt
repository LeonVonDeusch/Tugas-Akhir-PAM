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
    private var items: List<Pair<Comment, Int>> = emptyList(),
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

    /**
     * Dipanggil dari Activity saat ViewModel mengirim data baru.
     * Menggantikan pola lama yang membuat ulang seluruh adapter.
     */
    fun getItem(position: Int): Pair<Comment, Int> = items[position]

    fun updateComments(newItems: List<Pair<Comment, Int>>) {
        items = newItems
        notifyDataSetChanged()
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

        val params = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (level > 0) {
            params.marginStart = (level * 48).dpToPx(holder.itemView.context)
            params.marginEnd = 8.dpToPx(holder.itemView.context)
        } else {
            params.marginStart = 8.dpToPx(holder.itemView.context)
            params.marginEnd = 8.dpToPx(holder.itemView.context)
        }
        holder.itemView.layoutParams = params

        holder.tvContent.text = comment.content
        holder.tvDate.text = comment.created_at ?: ""

        holder.btnReply.setOnClickListener { onReplyClick(comment) }
        holder.btnEdit.setOnClickListener { onEditClick(comment) }
        holder.btnDelete.setOnClickListener { onDeleteClick(comment) }

        if (comment.parent != null) {
            holder.tvReplyTo?.visibility = View.VISIBLE
            holder.tvReplyTo?.text = "Reply to: ${comment.parent.content}"
        } else {
            holder.tvReplyTo?.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].first.parent_id == null) TYPE_COMMENT else TYPE_REPLY
    }
}

fun Int.dpToPx(context: android.content.Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}