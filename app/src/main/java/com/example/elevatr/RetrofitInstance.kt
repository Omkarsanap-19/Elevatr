package com.example.elevatr

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val base_url="https://api.scrapingdog.com/"
const val API_KEY ="6896ea620444ed19a669203f"

object RetrofitInstance {

    val newsInstance : API_interface
    init {

        val retrofit= Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        newsInstance = retrofit.create(API_interface::class.java)
    }
}