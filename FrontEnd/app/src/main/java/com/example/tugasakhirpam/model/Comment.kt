package com.example.tugasakhirpam.model

data class Comment(
    val id: String? = null,
    val user_id: String,
    val item_id: String,
    val item_type: String,
    val content: String,
    val parent_id: String? = null,
    val parent: Comment? = null,
    var children: MutableList<Comment> = mutableListOf(),
    val created_at: String? = null
)