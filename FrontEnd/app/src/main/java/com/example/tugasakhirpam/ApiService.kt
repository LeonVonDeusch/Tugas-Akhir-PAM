package com.example.tugasakhirpam

import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.model.CommentResponse
import com.example.tugasakhirpam.model.SingleCommentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("api/v1/comments/{item_type}/{item_id}")
    fun getComments(
        @Path("item_type") itemType: String,
        @Path("item_id") itemId: String
    ): Call<CommentResponse>

    @POST("api/v1/comment")
    fun createComment(
        @Body comment: Comment
    ): Call<SingleCommentResponse>
}