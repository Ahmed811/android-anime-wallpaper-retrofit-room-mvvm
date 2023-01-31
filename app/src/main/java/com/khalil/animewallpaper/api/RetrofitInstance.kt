package com.khalil.animewallpaper.api

import com.khalil.animewallpaper.util.Constant.Companion.ANIME_BASE_URL

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{
//        private lateinit var baseUrl: String
        private val retrofit by lazy {
            val logging=HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client=OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(ANIME_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        val api by lazy {
            retrofit.create(AnimeApi::class.java)
        }
//        fun init(baseUrl: String) {
//            this.baseUrl = baseUrl
//        }
    }


}