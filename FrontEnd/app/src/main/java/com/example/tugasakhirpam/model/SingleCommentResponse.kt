package com.example.tugasakhirpam.model

data class SingleCommentResponse(
    val success: Boolean,
    val message: String,
    val data: Comment
)