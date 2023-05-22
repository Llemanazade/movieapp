package com.example.movieapplication.features.moviefeature.view.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapplication.R
import com.example.movieapplication.databinding.FragmentMovieDetailsBinding
import com.example.movieapplication.databinding.MediaActorBinding
import com.example.movieapplication.features.moviefeature.model.MediaCast
import com.example.movieapplication.features.moviefeature.model.MediaDetailsResponse
import com.example.movieapplication.features.moviefeature.view.adapters.MediaAdapter
import com.example.movieapplication.features.moviefeature.viewmodel.DetailsViewModel
import com.example.movieapplication.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movie_details.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MediaDetailsFragment : Fragment() {
    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var binding: FragmentMovieDetailsBinding
    private val args: MediaDetailsFragmentArgs by navArgs()
    private val artistAdapter by lazy { MediaAdapter<MediaCast>() }
    private var artistList: MutableList<MediaCast> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMovieDetailsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (args.mediaType) {
            "Movies" -> {
                viewModel.getMovieDetails(args.id)
                observeMovieArtists(args.id)
                viewModel.movieDetail?.observe(viewLifecycleOwner) { responseResource ->
                    when (responseResource) {
                        is Resource.Success -> {
                            binding.prgBarMovies.isVisible = false
                            responseResource.data?.let { movieDetailsResponse ->
                                val movie = movieDetailsResponse.toMediaDetails()
                                setViews(movie)
                                setColors(movie)
                                setToolbar()
                                setupArtistRecyclerView(artistAdapter)
                                setupArtistRecyclerViewAdapter(artistAdapter)
                            }
                        }

                        is Resource.Error -> {
                            binding.prgBarMovies.isVisible = false
                            responseResource.message?.let { message ->
                                Toast.makeText(
                                    requireContext(),
                                    "An error occurred: $message",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        is Resource.Loading -> {
                            binding.prgBarMovies.isVisible = true
                        }
                    }
                }
            }

            "Series" -> {
                viewModel.getSerieDetails(args.id)
                observeSeriesArtists(args.id)
                viewModel.seriesDetail?.observe(viewLifecycleOwner) { responseResource ->
                    when (responseResource) {
                        is Resource.Success -> {
                            binding.prgBarMovies.isVisible = false
                            responseResource.data?.let { serieDetailsResponse ->

                                val serie = serieDetailsResponse.toMediaDetails()
                                setViews(serie)
                                setColors(serie)
                                setToolbar()
                                setupArtistRecyclerView(artistAdapter)
                                setupArtistRecyclerViewAdapter(artistAdapter)
                            }
                        }

                        is Resource.Error -> {
                            binding.prgBarMovies.isVisible = false
                            responseResource.message?.let { message ->
                                Toast.makeText(
                                    requireContext(),
                                    "An error occurred: $message",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        is Resource.Loading -> {
                            binding.prgBarMovies.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun setViews(details: MediaDetailsResponse) {
        binding.tvMovieDateRelease.text = details.release_date
        binding.tvMovieTitle.text = details.title
        binding.tvMovieTagLine.text = details.original_title
        binding.tvMediaOverview.text = details.overview
        details.poster_path?.let { setImage(binding.imgMovieImage, it, requireContext()) }
        details.backdrop_path?.let { setImage(binding.imgMovieBack, it, requireContext()) }
        binding.tvMovieGenre.text = setDetailsGenre(details)
        binding.prgBarMovies.isVisible = false
    }

    private fun setToolbar() {
        (activity as AppCompatActivity).apply {
            val toolbar = this.toolbar
            toolbar.isEnabled
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setColors(details: MediaDetailsResponse) {
        CoroutineScope(Dispatchers.Default).launch {
            view?.let {
                details.poster_path?.let { it1 ->
                    requireView().clDetails.assignColors(
                        requireContext(),
                        it1,
                        glide(requireContext()),
                        it.tvMediaOverview,
                        requireView().tvMovieGenre,
                        requireView().tvMovieDateRelease,
                        requireView().tvMovieTitle,
                        requireView().tvMovieTagLine
                    )
                }
            }
        }
    }

    private fun setupArtistRecyclerViewAdapter(adapter: MediaAdapter<MediaCast>) {
        adapter.expressionOnCreateViewHolder = { viewGroup ->
            MediaActorBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        adapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as MediaActorBinding
            view.tvActorName.text = eachItem.Name
            view.tvCharacter.text = eachItem.Character
            view.tvRate.text = eachItem.rate.toString()

            if (eachItem.profile != null) {
                setImage(view.ImgActor, eachItem.profile, this.requireContext())
                CoroutineScope(Dispatchers.IO).launch {
                    eachItem.profile?.let {
                        view.mediaCardView.assignColors(
                            requireContext(),
                            it,
                            glide(requireContext()),
                            view.tvRate,
                            view.tvActorName,
                            view.tvCharacter,
                            view.tvRate,
                        )
                    }
                }
            } else view.ImgActor.setImageResource(R.drawable.ic_baseline_people_alt_24)


            view.root.setOnClickListener {
                val action = R.id.action_mediaDetailsFragment_to_personDetails
                val bundle = Bundle().apply {
                    putInt("id", eachItem.Id)

                }
                findNavController().navigate(action, bundle)
            }
        }
    }

    private fun setupArtistRecyclerView(thisadapter: MediaAdapter<MediaCast>) {
        binding.rvArtists.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = thisadapter
        }
    }

    private fun observeMovieArtists(id: Int) {
        viewModel.getMovieCredits(id)
        viewModel.movieCredit.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curArtistlist = resourceResponse.data?.cast?.map { it.toMediaCast() }
                    if (curArtistlist != null) {
                        artistList.addAll(curArtistlist.toMutableList())
                        artistAdapter.differ.submitList(artistList)
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

    private fun observeSeriesArtists(id: Int) {
        viewModel.getSeriesCredits(id)
        viewModel.seriesCredit.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curArtistlist = resourceResponse.data?.cast?.map { it.toMediaCast() }
                    if (curArtistlist != null) {
                        artistList.addAll(curArtistlist.toMutableList())
                        artistAdapter.differ.submitList(artistList)
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
}

