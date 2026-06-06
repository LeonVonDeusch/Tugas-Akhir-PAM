package com.example.tugasakhirpam.model

data class CommentResponse(
    val success: Boolean,
    val message: String,
    val data: List<Comment>
)