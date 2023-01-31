package com.khalil.animewallpaper.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import  com.khalil.animewallpaper.model.anime.Result


@Dao
interface WallpaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(result:Result):Long

    @Query("SELECT * FROM wallpapers")
    fun getWallpapers():LiveData<List<Result>>

    @Delete
    suspend fun deleteWallpapers(result:Result)
}