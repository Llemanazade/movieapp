package com.example.movieapplication.features.moviefeature.view.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.movieapplication.R
import com.example.movieapplication.databinding.FragmentSeriesBinding
import com.example.movieapplication.databinding.MovieListBinding
import com.example.movieapplication.features.moviefeature.model.MediaGenre
import com.example.movieapplication.features.moviefeature.model.MediaResult
import com.example.movieapplication.features.moviefeature.view.adapters.MediaAdapter
import com.example.movieapplication.features.moviefeature.view.adapters.MediaPagerAdapter
import com.example.movieapplication.features.moviefeature.viewmodel.MediaViewModel
import com.example.movieapplication.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_series.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeriesFragment : Fragment() {
    private lateinit var binding: FragmentSeriesBinding
    private val viewModel: MediaViewModel by activityViewModels()
    private val topSeriesAdapter by lazy { MediaAdapter<MediaResult>() }
    private val popularSeriesAdapter by lazy { MediaPagerAdapter<MediaResult>() }
    private var genreList: MutableList<MediaGenre> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getSeriesGenres()
        lifecycleScope.launchWhenResumed {
            observeData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSeriesBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopRecyclerView()
        setupPopularRecyclerView()
        PagerSnapHelper().attachToRecyclerView(rvTopSeries)
        setupTopAdapter()
        setupPopularAdapter()

    }

    private fun setupTopAdapter() {

        topSeriesAdapter.expressionOnCreateViewHolder = { viewGroup ->
            MovieListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }
        topSeriesAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as MovieListBinding
            view.tvMovieName.text = eachItem.title
            view.tvMovieDateRelease.text = eachItem.release_date
            view.tvRate.text = eachItem.vote_average.toString()
            view.tvLang.text = eachItem.original_language
            view.prgBarMovies.isVisible = false
            setImage(view.ImgMovie, eachItem.poster_path, this.requireContext())

            view.tvGenre.text = setGenres(eachItem, genreList)
            CoroutineScope(Dispatchers.Default).launch {
                view.mediaCardView.assignColors(
                    requireContext(),
                    eachItem.poster_path,
                    glide(requireContext()),
                    view.tvRate,
                    view.tvGenre,
                    view.tvLang,
                    view.tvMovieName,
                    view.tvMovieDateRelease
                )
            }

            view.root.setOnClickListener {
                val action = R.id.action_seriesFragment_to_mediaDetailsFragment
                val bundle = Bundle().apply {
                    putInt("id", eachItem.id)
                    putString("mediaType", "Series")
                }
                findNavController().navigate(action, bundle)

            }
        }
    }

    private fun setupTopRecyclerView() {
        binding.rvTopSeries.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topSeriesAdapter
        }
    }

    private fun setupPopularAdapter() {

        popularSeriesAdapter.expressionOnCreateViewHolder = { viewGroup ->
            MovieListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        popularSeriesAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as MovieListBinding
            view.tvMovieName.text = eachItem.title
            view.tvMovieDateRelease.text = eachItem.release_date
            view.tvRate.text = eachItem.vote_average.toString()
            view.tvLang.text = eachItem.original_language
            view.prgBarMovies.isVisible = false
            setImage(view.ImgMovie, eachItem.poster_path, this.requireContext())

            view.tvGenre.text = setGenres(eachItem, genreList)

            CoroutineScope(Dispatchers.Default).launch {
                view.mediaCardView.assignColors(
                    requireContext(),
                    eachItem.poster_path,
                    glide(requireContext()),
                    view.tvRate,
                    view.tvGenre,
                    view.tvLang,
                    view.tvMovieName,
                    view.tvMovieDateRelease
                )
            }

            view.root.setOnClickListener {
                val action = R.id.action_seriesFragment_to_mediaDetailsFragment
                val bundle = Bundle().apply {
                    putInt("id", eachItem.id)
                    putString("mediaType", "Series")
                }
                findNavController().navigate(action, bundle)
            }
        }
    }

    private fun setupPopularRecyclerView() {
        binding.rvPopularSeries.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popularSeriesAdapter
        }
    }

    private fun observeData() {

        viewModel.seriesGenre.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curGenreList = resourceResponse.data?.genres?.map { it.toMediaGenre() }

                    if (curGenreList != null) {
                        genreList.addAll(curGenreList.toMutableList())
                        viewModel.getTopRatedSeries()
                        observeTopRatedSeries()
                        observePopularSeries()
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.e(ContentValues.TAG, "An error occurred: $message")
                        Toast.makeText(requireContext(),
                            "An error occurred: $message",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }
    }

    private fun observePopularSeries() {
        lifecycleScope.launch {
            viewModel.getPopularSeries().observe(viewLifecycleOwner) {
                it?.let {
                    val mediaPagingData = it.map { result ->
                        result.toResult()
                    }
                    popularSeriesAdapter.submitData(lifecycle, mediaPagingData)
                }
            }
        }
    }

    private fun observeTopRatedSeries() {
        viewModel.topSeries.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    resourceResponse.data?.let { response ->
                        topSeriesAdapter.differ.submitList(response.toResponse().results.toMutableList())
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.e(ContentValues.TAG, "An error occurred: $message")
                    }
                }

                else -> {}
            }

        }
    }
}
