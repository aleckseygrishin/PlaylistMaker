package com.practicum.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCreate(private val href: String) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(href)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun createdRetrofit(): ITunesApi {
        return retrofit.create(ITunesApi::class.java)
    }
}