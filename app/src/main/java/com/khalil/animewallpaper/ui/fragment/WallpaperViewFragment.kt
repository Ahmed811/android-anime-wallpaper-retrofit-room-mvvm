package com.khalil.animewallpaper.ui.fragment

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.khalil.animewallpaper.R
import com.khalil.animewallpaper.databinding.FragmentWallpaperViewBinding
import com.khalil.animewallpaper.ui.MainActivity
import com.khalil.animewallpaper.ui.WallpaperViewModel
import com.khalil.animewallpaper.util.Constant.Companion.HOME_SCREEN
import com.khalil.animewallpaper.util.Constant.Companion.LOCK_SCREEN
import java.io.File
import java.util.Calendar
import com.khalil.animewallpaper.model.anime.Result
import kotlinx.coroutines.*

class WallpaperViewFragment : Fragment() {
    lateinit var viewModel: WallpaperViewModel
    lateinit var binding: FragmentWallpaperViewBinding
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var adRequest:AdRequest
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentWallpaperViewBinding.inflate(inflater,container,false)
        return (binding.root)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        val bundle=this.arguments
        val wallpaper=bundle?.getSerializable("wallpaper") as Result
//        Log.d("wallpaper",wallpaperUrl.toString())
        Glide.with(requireContext()).load(wallpaper.url).into( binding.ivWallpaperView)
         adRequest = AdRequest.Builder().build()
        binding.ibDownload.setOnClickListener {
            downloadWallpaper(wallpaper.url)
        }
        
        binding.ibSetWallpaper.setOnClickListener {
            showDialoge()
           // setWallpaper(HOME_SCREEN)
        }
        binding.ibFavorite.setOnClickListener {
            viewModel.saveWallpaper(wallpaper)
            Snackbar.make(view,"${getString(R.string.successfully_saved)}",Snackbar.LENGTH_LONG).show()
        }
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),"${getString(R.string.interstitial_ad_test)}", adRequest, object :
            InterstitialAdLoadCallback() {

            })


    }


    private fun showDialoge() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.home_or_lock))
        builder.setMessage(getString(R.string.set_wallpaper))
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(getString(R.string.home_screen)) { dialog, which ->
           setWallpaper(HOME_SCREEN)
            showFullAd()
        }

        builder.setNegativeButton(getString(R.string.lock_screen)) { dialog, which ->
            setWallpaper(LOCK_SCREEN)
            showFullAd()
        }

        builder.setNeutralButton(getString(R.string.homescreen_and_lockscreen)) { dialog, which ->
           setWallpaper(HOME_SCREEN)
            setWallpaper(LOCK_SCREEN)
            showFullAd()
        }
        builder.show()
    }

    private fun setWallpaper(LockOrBackground: Int) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            try {
            val wallpaperManager=WallpaperManager.getInstance(requireContext())
                if (binding.ivWallpaperView.drawable==null){
                    Toast.makeText(requireContext(),getString(R.string.wait_to_download),Toast.LENGTH_LONG).show()

                }else{
                    val bitmap=(binding.ivWallpaperView.drawable as BitmapDrawable).bitmap
                    wallpaperManager.setBitmap(bitmap,null,true,LockOrBackground)
                    Toast.makeText(requireContext(),"${getString(R.string.successfully_set_wallpaper)} \uD83D\uDE0A ",Toast.LENGTH_LONG).show()


                }
            }catch (e:Exception){
                Toast.makeText(requireContext(),"${e.message}",Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun downloadWallpaper(wallpaperUrl: String?) {
    try {
val downloadManager=requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val imgUrl=Uri.parse(wallpaperUrl)
        val request=DownloadManager.Request(imgUrl).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setMimeType("image/*")
            setAllowedOverRoaming(false)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setTitle("wallpaper"+Calendar.DATE.toString())
            setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,File.separator+"wallpaper"+".jpg")

        }
        downloadManager.enqueue(request)
        Toast.makeText(requireContext(),getString(R.string.downloading),Toast.LENGTH_LONG).show()
        showFullAd()
    }catch (e:Exception){
        Toast.makeText(requireContext(),"${getString(R.string.error_during_download)} ${e.message}",Toast.LENGTH_LONG).show()
    }


    }
    fun showFullAd(){
        InterstitialAd.load(requireContext(),getString(R.string.interstitial_ad), adRequest!!, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("ads", adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("ads", "Ad was loaded.")

                mInterstitialAd = interstitialAd
                mInterstitialAd?.show(requireActivity())
            }
        })
//        if (mInterstitialAd != null) {
//            mInterstitialAd?.show(requireActivity())
//        } else {
//            Log.d("TAG", "The interstitial ad wasn't ready yet.")
//        }
    }
}