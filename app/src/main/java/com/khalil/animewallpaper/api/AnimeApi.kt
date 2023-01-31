package com.khalil.animewallpaper.api


import com.khalil.animewallpaper.model.anime.AnimeResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AnimeApi {
    @GET("neko")
    suspend fun getAnimeGirlWallpaper(
        @Query("amount") amount: Int=20,

    ):Response<AnimeResponse>

    @GET("husbando")
    suspend fun getAnimeBoyWallpaper(
        @Query("amount") amount: Int=20,

    ):Response<AnimeResponse>

//    @Headers(
//        "Accept: application/json",
//        "Authorization: 563492ad6f9170000100000115fe048e9b354434bd96ad9d061c2f68"
//    )
//    @GET("curated/")
//    suspend fun getPopularWallpaper(
//        @Query("page") page: String,
//        @Query("per_page") per_page:String
//    ):Response<PopularResponse>
}