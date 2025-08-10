package com.example.elevatr

import com.example.elevatr.res.ResponseList
import retrofit2.http.GET
import retrofit2.http.Query

interface API_interface {


        @GET("linkedin/")
        suspend fun getLinkedInProfile(
            @Query("api_key") apiKey: String,
            @Query("type") type: String = "profile",
            @Query("linkId") linkId: String
        ): ResponseList


}