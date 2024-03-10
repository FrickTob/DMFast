package com.example.dmfast.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("monsters")
    fun getMonsters() : Call<MonsterList>

    @GET("monsters/{id}")
    fun getMonsterByID(@Path("id") monsterId : String) : Call<DetailedMonster>
}