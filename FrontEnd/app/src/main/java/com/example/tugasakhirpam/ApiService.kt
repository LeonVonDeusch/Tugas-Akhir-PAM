package com.example.tugasakhirpam

import com.example.tugasakhirpam.model.Comment
import com.example.tugasakhirpam.model.CommentResponse
import com.example.tugasakhirpam.model.GeneralResponse
import com.example.tugasakhirpam.model.SingleCommentResponse
import com.example.tugasakhirpam.model.UpdateCommentRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @PUT("api/v1/comment/{id}")
    fun updateComment(
        @Path("id") id: String,
        @Body request: UpdateCommentRequest
    ): Call<SingleCommentResponse>

    @DELETE("api/v1/comment/{id}")
    fun deleteComment(
        @Path("id") id: String
    ): Call<GeneralResponse>
}