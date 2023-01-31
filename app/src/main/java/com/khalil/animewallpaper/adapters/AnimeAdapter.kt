package com.khalil.animewallpaper.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.khalil.animewallpaper.R
import com.khalil.animewallpaper.databinding.WallpaperImageRowBinding
import com.khalil.animewallpaper.model.anime.Result


class AnimeAdapter: RecyclerView.Adapter<AnimeAdapter.ViewHolder>() {
    var _binding:WallpaperImageRowBinding?=null
    inner class ViewHolder(binding:WallpaperImageRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    private val differCallBack=object:DiffUtil.ItemCallback<Result>(){
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
return oldItem.url==newItem.url
                    }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
return oldItem==newItem
        }

    }
    val differ=AsyncListDiffer(this@AnimeAdapter,differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding=WallpaperImageRowBinding.inflate(LayoutInflater.from(parent.context))
       return ViewHolder(_binding!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val image=differ.currentList[position]
        holder.itemView.apply {
            _binding!!.lottieLoading.setAnimation(R.raw.loading)
            _binding!!.lottieLoading.loop(true)
            _binding!!.lottieLoading.playAnimation()


       Glide.with(this).load(image.url).into(_binding!!.imgWallpaper)

            setOnClickListener {
                onItemclickListener?.let { it(image) }
            }
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    private var onItemclickListener:((Result)->Unit)?=null

    fun setOnItemClickListener(listener:(Result)->Unit){
       onItemclickListener=listener
    }
}