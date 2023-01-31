package com.khalil.animewallpaper.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.khalil.animewallpaper.model.anime.Result


@Database(
    entities = [Result::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class WallpaperDatabase: RoomDatabase() {
    abstract fun getWallpaperDao(): WallpaperDao
    companion object{
    @Volatile
    private var instance:WallpaperDatabase?=null
        private val Lock=Any()
        operator fun invoke(context:Context)= instance ?: synchronized(Lock){
            instance?:createDatabase(context).also { instance=it}
        }


        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                WallpaperDatabase::class.java,
                "wallpaper_db.db"
            ).build()
    }



}