package com.khalil.animewallpaper.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.net.ConnectivityManager.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khalil.animewallpaper.WallpaperApplication
import com.khalil.animewallpaper.model.anime.AnimeResponse
import  com.khalil.animewallpaper.model.anime.Result

import com.khalil.animewallpaper.repository.WallPaperRepository
import com.khalil.animewallpaper.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class WallpaperViewModel(app: Application,
                         val repository: WallPaperRepository
): AndroidViewModel(app) {
    val animeWallpaper: MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()
    var wallpaperResponse:AnimeResponse?=null
    val animeBoyWallpaper: MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()
    var BoyWallpaperResponse:AnimeResponse?=null

    init {
    getAnimeWallpaper(20)
    getAnimeBoyWallpaper(20)

}

    //db
    fun saveWallpaper(result: Result)=viewModelScope.launch {
       repository.upsert(result)
    }

    fun getSavedWallpapers()=repository.getSavedWallpaper()
    fun deleteWallpaper(result: Result)=viewModelScope.launch {
       repository.deleteWallpaper(result)
    }
    //api
    fun getAnimeWallpaper(amount:Int){
        viewModelScope.launch{
//       animeWallpaper.postValue(Resource.Loading())
//            val response=repository.getAnimeGirlWallpaper(amount)
//           animeWallpaper.postValue(handleAnimeWallpaperResponse(response))
            safeGirlWallpaperCall(amount)
        }
    }
    fun getAnimeBoyWallpaper(amount:Int){
        viewModelScope.launch{
//       animeBoyWallpaper.postValue(Resource.Loading())
//            val response=repository.getAnimeBoyWallpaper(amount)
//            animeBoyWallpaper.postValue(handleAnimeBoyWallpaperResponse(response))
            safeBoyWallpaperCall(amount)
        }
    }

    private fun handleAnimeWallpaperResponse(response: Response<AnimeResponse>): Resource<AnimeResponse>? {
        if(response.isSuccessful){
            response.body()?.let {resultResponse->

                    if (wallpaperResponse==null){
                        wallpaperResponse=resultResponse
                    }else{
                        val oldWallpaper=wallpaperResponse?.results
                        val newWallpaper=resultResponse.results
                        oldWallpaper?.addAll(newWallpaper)
                    }
                return Resource.Success(wallpaperResponse?:resultResponse)
                }
                }


        return Resource.Error(response.message())
        }
    private fun handleAnimeBoyWallpaperResponse(response: Response<AnimeResponse>): Resource<AnimeResponse>? {
        if(response.isSuccessful){
            response.body()?.let {resultResponse->

                    if (BoyWallpaperResponse==null){
                        BoyWallpaperResponse=resultResponse
                    }else{
                        val oldWallpaper=BoyWallpaperResponse?.results
                        val newWallpaper=resultResponse.results
                        oldWallpaper?.addAll(newWallpaper)
                    }
                return Resource.Success(BoyWallpaperResponse?:resultResponse)
                }
                }


        return Resource.Error(response.message())
        }



    private suspend fun safeGirlWallpaperCall(amount: Int) {
        animeWallpaper.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = repository.getAnimeGirlWallpaper(amount)
                animeWallpaper.postValue(handleAnimeWallpaperResponse(response))
            } else {
                animeWallpaper.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> animeWallpaper.postValue(Resource.Error("Network Failure"))
                else -> animeWallpaper.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private suspend fun safeBoyWallpaperCall(amount: Int) {
        animeBoyWallpaper.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = repository.getAnimeBoyWallpaper(amount)
                animeBoyWallpaper.postValue(handleAnimeBoyWallpaperResponse(response))
            } else {
                animeBoyWallpaper.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> animeBoyWallpaper.postValue(Resource.Error("Network Failure"))
                else -> animeBoyWallpaper.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<WallpaperApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
    }
