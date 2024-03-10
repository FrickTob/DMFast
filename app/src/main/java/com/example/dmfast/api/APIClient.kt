package com.example.dmfast.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
        private const val BASE_URL = "https://www.dnd5eapi.co/api/"

        val retrofit: Retrofit by lazy {
                Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
        }
}

object APIClient {
        val apiService : ApiService by lazy {
                RetrofitClient.retrofit.create(ApiService::class.java)
        }
}