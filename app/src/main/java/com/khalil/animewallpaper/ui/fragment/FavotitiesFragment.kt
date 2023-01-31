package com.khalil.animewallpaper.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.khalil.animewallpaper.R
import com.khalil.animewallpaper.adapters.AnimeAdapter
import com.khalil.animewallpaper.databinding.FragmentAnimeWallpaperListBinding
import com.khalil.animewallpaper.databinding.FragmentFavotitiesBinding
import com.khalil.animewallpaper.ui.MainActivity
import com.khalil.animewallpaper.ui.WallpaperViewModel


class FavotitiesFragment : Fragment() {
    lateinit var _binding:FragmentFavotitiesBinding
    lateinit var viewModel: WallpaperViewModel
    lateinit var animeAdapter: AnimeAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentFavotitiesBinding.inflate(inflater,container,false)
        return (_binding.root)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        setUpRecyclerView()
        animeAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("wallpaper",it)
            }
            findNavController().navigate(R.id.action_favotitiesFragment_to_wallpaperViewFragment,bundle)
        }


        //drag on recyclerView
        val touchItemHelpercallback=object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val wallpaper=animeAdapter.differ.currentList[position]
                viewModel.deleteWallpaper(wallpaper)
                Snackbar.make(view,"${getString(R.string.successfully_deleted)}",Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo)){
                        viewModel.saveWallpaper(wallpaper)
                    }
                }
            }

        }
        ItemTouchHelper(touchItemHelpercallback).apply {
            attachToRecyclerView(_binding.rcFavoriteList)
        }
        viewModel.getSavedWallpapers().observe(viewLifecycleOwner, Observer {articles->
            animeAdapter.differ.submitList(articles)
        })


    }
    private fun setUpRecyclerView() {
        animeAdapter= AnimeAdapter()
        _binding.rcFavoriteList.apply {
            adapter=animeAdapter
            layoutManager= GridLayoutManager(activity,2)
        }

    }

//    private fun hideProgressBar(){
//        paginationProgressBar.visibility=View.INVISIBLE
//    }
//    private fun showProgressBar(){
//        paginationProgressBar.visibility=View.VISIBLE
//    }
}