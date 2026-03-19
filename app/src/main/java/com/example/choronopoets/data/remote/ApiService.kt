package com.example.choronopoets.data.remote

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.HTTP

interface ApiService {
    @HTTP(method = "POST", path = "/ask", hasBody = true)
    @Headers("Content-Type: application/json")
    suspend fun getAnswerForQuestion(
        @Body question: RequestBody,
    ): ResponseBody
}
