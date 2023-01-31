package com.khalil.animewallpaper.model.anime

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class Result(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    val artist_href: String,
    val artist_name: String,
    val source_url: String,
    val url: String
):java.io.Serializable