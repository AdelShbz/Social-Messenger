package com.example.socialmessenger

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
//import retrofit2.http.DELETE
//import retrofit2.http.GET
//import retrofit2.http.Header
import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Path

interface ApiService {

    @POST("/user/register")
    fun postRegisterUser(@Body user: User): Call<ResponseBody>

    @POST("/user/login")
    fun postLoginUser(@Body user:User): Call<ResponseBody>

    @GET("/contact-list")
    fun getContactList(@Header("Authorization") token: String) : Call<ResponseBody>

    @GET("/chat-list")
    fun getChatList(@Header("Authorization") token: String) : Call<ResponseBody>
}