package com.khalil.animewallpaper.repository


import com.khalil.animewallpaper.api.RetrofitInstance
import com.khalil.animewallpaper.db.WallpaperDatabase
import com.khalil.animewallpaper.util.Constant
import  com.khalil.animewallpaper.model.anime.Result


class WallPaperRepository(
    val db:WallpaperDatabase
) {

//by retrofit
    suspend fun getAnimeGirlWallpaper(amount:Int)=RetrofitInstance.api.getAnimeGirlWallpaper(amount)
    suspend fun getAnimeBoyWallpaper(amount:Int)=RetrofitInstance.api.getAnimeBoyWallpaper(amount)

    //by room
    //by room
    suspend fun upsert(result:Result)=db.getWallpaperDao().upsert(result)
    fun getSavedWallpaper()=db.getWallpaperDao().getWallpapers()
    suspend fun deleteWallpaper(result:Result)=db.getWallpaperDao().deleteWallpapers(result)
//    suspend fun getAnimeWallpaper(many:Boolean,is_nsfw:Boolean){
//        val retrofitInstance=RetrofitInstance()
//        retrofitInstance.init(Constant.ANIME_BASE_URL)
//        retrofitInstance.api.getAnimeWallpaper(many,is_nsfw)
//    }
}

