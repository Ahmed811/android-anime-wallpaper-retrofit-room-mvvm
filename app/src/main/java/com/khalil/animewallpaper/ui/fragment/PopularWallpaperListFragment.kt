package com.khalil.animewallpaper.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khalil.animewallpaper.R
import com.khalil.animewallpaper.adapters.AnimeAdapter
import com.khalil.animewallpaper.databinding.FragmentAnimeWallpaperListBinding
import com.khalil.animewallpaper.databinding.FragmentPopularWallpaperListBinding
import com.khalil.animewallpaper.ui.MainActivity
import com.khalil.animewallpaper.ui.WallpaperViewModel
import com.khalil.animewallpaper.util.Constant
import com.khalil.animewallpaper.util.Resource


class PopularWallpaperListFragment : Fragment() {
    lateinit var _binding:FragmentPopularWallpaperListBinding
    lateinit var viewModel: WallpaperViewModel
    lateinit var animeAdapter: AnimeAdapter
    val TAG="PopularWallpaperListFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentPopularWallpaperListBinding.inflate(inflater,container,false)
        return (_binding.root)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        setUpRecyclerView()
        animeAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("wallpaper",it)
                Log.d("wallpaperUrl",it.url)
            }
            findNavController().navigate(R.id.action_popularWallpaperListFragment_to_wallpaperViewFragment,bundle)
        }
        viewModel.animeBoyWallpaper.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    hideErrorCard()
                    //    hideErrorMessage()
                    response.data?.let {animeResponse->
                        animeAdapter.differ.submitList(animeResponse.results.toList())

                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let {message->

                        Toast.makeText(requireContext(), "${getString(R.string.error)} $message", Toast.LENGTH_LONG).show()
                        showErrorCard()
                        //  showErrorMessage(message)
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }

        })
    }
    private fun showProgressBar() {
        _binding.progressBar.visibility=View.VISIBLE
    }

    private fun hideProgressBar(){
        _binding.progressBar.visibility=View.INVISIBLE
    }
    fun showErrorCard(){
        _binding.cvError. visibility=View.VISIBLE
        _binding.btnRetry.setOnClickListener {
            viewModel.getAnimeBoyWallpaper(20)
            hideErrorCard()
        }
    }
    fun hideErrorCard(){
        _binding.cvError. visibility=View.INVISIBLE
    }
    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constant.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.getAnimeBoyWallpaper(20)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
    private fun setUpRecyclerView(){
        animeAdapter= AnimeAdapter()

        _binding.rcAnimeBoyList.apply {
            adapter=animeAdapter
            layoutManager= GridLayoutManager(requireContext(),2)
            setHasFixedSize(true)
            addOnScrollListener(this@PopularWallpaperListFragment.scrollListener)
        }

    }
}