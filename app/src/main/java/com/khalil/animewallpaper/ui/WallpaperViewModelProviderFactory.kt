package com.khalil.animewallpaper.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khalil.animewallpaper.repository.WallPaperRepository

class WallpaperViewModelProviderFactory(val app:Application,val wallPaperRepository: WallPaperRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WallpaperViewModel(app,wallPaperRepository) as T
    }
}