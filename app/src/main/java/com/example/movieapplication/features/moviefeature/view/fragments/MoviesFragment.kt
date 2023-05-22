package com.example.movieapplication.features.moviefeature.view.fragments

import android.content.ContentValues.TAG
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
import com.example.movieapplication.databinding.FragmentMoviesBinding
import com.example.movieapplication.databinding.MovieListBinding
import com.example.movieapplication.features.moviefeature.model.MediaGenre
import com.example.movieapplication.features.moviefeature.model.MediaResult
import com.example.movieapplication.features.moviefeature.view.adapters.MediaAdapter
import com.example.movieapplication.features.moviefeature.view.adapters.MediaPagerAdapter
import com.example.movieapplication.features.moviefeature.viewmodel.MediaViewModel
import com.example.movieapplication.utility.*
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MoviesFragment : Fragment() {
    private lateinit var binding: FragmentMoviesBinding
    private val viewModel: MediaViewModel by activityViewModels()
    private val topMoviesAdapter by lazy { MediaAdapter<MediaResult>() }
    private val popularMoviesAdapter by lazy { MediaPagerAdapter<MediaResult>() }
    private var genreList: MutableList<MediaGenre> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.getMovieGenres()
            observeData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMoviesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        viewModel.getTopRatedMovies()
        super.onViewCreated(view, savedInstanceState)
        PagerSnapHelper().attachToRecyclerView(rvTopMovies)

        setupTopRecyclerView()
        setupPopularRecyclerView()
        setupTopAdapter()
        setupPopularAdapter()
    }

    private fun setupTopAdapter() {

        topMoviesAdapter.expressionOnCreateViewHolder = { viewGroup ->
            MovieListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        topMoviesAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as MovieListBinding
            view.tvMovieName.text = eachItem.title
            view.tvMovieDateRelease.text = eachItem.release_date
            view.tvRate.text = eachItem.vote_average.toString()
            view.tvLang.text = eachItem.original_language
            setImage(view.ImgMovie, eachItem.poster_path, this.requireContext())
            view.tvGenre.text = setGenres(eachItem, genreList)
            view.prgBarMovies.isVisible = false

            CoroutineScope(Dispatchers.IO).launch {
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
                val action = R.id.action_moviesFragment_to_mediaDetailsFragment
                val bundle = Bundle().apply {
                    putInt("id", eachItem.id)
                    putString("mediaType", "Movies")

                }
                findNavController().navigate(action, bundle)
            }
        }
    }

    private fun setupTopRecyclerView() {
        binding.rvTopMovies.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topMoviesAdapter
        }
    }

    private fun setupPopularAdapter() {

        popularMoviesAdapter.expressionOnCreateViewHolder = { viewGroup ->
            MovieListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        popularMoviesAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as MovieListBinding
            view.tvMovieName.text = eachItem.title
            view.tvMovieDateRelease.text = eachItem.release_date
            view.tvRate.text = eachItem.vote_average.toString()
            view.tvLang.text = eachItem.original_language
            setImage(view.ImgMovie, eachItem.poster_path, this.requireContext())
            view.tvGenre.text = setGenres(eachItem, genreList)
            view.prgBarMovies.isVisible = false
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
                val action = R.id.action_moviesFragment_to_mediaDetailsFragment
                val bundle = Bundle().apply {
                    putInt("id", eachItem.id)
                    putString("mediaType", "Movies")
                }
                findNavController().navigate(action, bundle)
            }
        }
    }

    private fun setupPopularRecyclerView() {
        binding.rvPopularMovies.adapter = popularMoviesAdapter
        binding.rvPopularMovies.layoutManager = LinearLayoutManager(context)
    }

    private fun observeData() {

        viewModel.movieGenre.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curGenreList = resourceResponse.data?.genres?.map { it.toMediaGenre() }
                    if (curGenreList != null) {
                        genreList.addAll(curGenreList.toMutableList())
                        viewModel.getTopRatedMovies()
                        observeTopRatedMovies()
                        observePopularMovies()
                    }
                }
                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.e(TAG, "An error occurred: $message")
                        Toast.makeText(requireContext(),
                            "An error occurred: $message",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }
    }

    private fun observePopularMovies() {
        lifecycleScope.launch {
            viewModel.getPopularMovies().observe(viewLifecycleOwner) {
                it?.let {
                    val mediaPagingData = it.map { result ->
                        result.toResult()
                    }
                    popularMoviesAdapter.submitData(lifecycle, mediaPagingData)
                }
            }
        }
    }

    private fun observeTopRatedMovies() {
        viewModel.topMovies.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    resourceResponse.data?.let { response ->
                        topMoviesAdapter.differ.submitList(response.toResponse().results.toMutableList())
                    }
                }
                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.e(TAG, "An error occurred: $message")
                    }
                }

                else -> {}
            }

        }
    }
}



