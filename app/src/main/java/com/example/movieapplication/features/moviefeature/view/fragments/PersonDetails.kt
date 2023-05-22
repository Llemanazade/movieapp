package com.example.movieapplication.features.moviefeature.view.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.R
import com.example.movieapplication.databinding.ActorMediaBinding
import com.example.movieapplication.databinding.FragmentPersonDetailsBinding
import com.example.movieapplication.features.moviefeature.model.*
import com.example.movieapplication.features.moviefeature.view.adapters.MediaAdapter
import com.example.movieapplication.features.moviefeature.viewmodel.MediaViewModel
import com.example.movieapplication.features.moviefeature.viewmodel.PeopleViewModel
import com.example.movieapplication.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonDetails : Fragment() {

    private val viewModel : PeopleViewModel by viewModels()
    private val mediaViewModel : MediaViewModel by viewModels()
    private lateinit var binding : FragmentPersonDetailsBinding
    private val args: PersonDetailsArgs by navArgs()
    private val movieCreditAdapter by lazy { MediaAdapter<PersonMediaCast>() }
    private val serieCreditAdapter by lazy { MediaAdapter<PersonMediaCast>() }
    private var movieCreditList: MutableList<PersonMediaCast> = mutableListOf()
    private var serieCreditList: MutableList<PersonMediaCast> = mutableListOf()
    private var movieGenreList: MutableList<MediaGenre> = mutableListOf()
    private var serieGenreList: MutableList<MediaGenre> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            mediaViewModel.getMovieGenres()
            mediaViewModel.getSeriesGenres()
            observeMovieData(args.id)
            observeSerieData(args.id)
            viewModel.getPersonDetails(args.id)
        }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPersonDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePersonData()
    }
    @SuppressLint("SetTextI18n")
    private fun setViews(person:Person){
        binding.tvPersonName.text=person.name
        binding.tvPersonBiography.text=person.biography
        binding.tvPopularity.text = person.rate.toString()
        if (person.gender==1) binding.tvGender.text= "female" else binding.tvGender.text="male"
        if (person.profilePath!=null)
            setImage(binding.ivPerson, person.profilePath, requireContext())

    }

    private fun setColors(item: Person, view: FragmentPersonDetailsBinding){
        CoroutineScope(Dispatchers.IO).launch {
            item.profilePath?.let {
                view.personDetailsLayout.assignColors(
                    requireContext(),
                    it,
                    glide(requireContext()),
                    view.tvPersonName,
                    view.tvGender,
                    view.tvPopularity,
                    view.tvPersonBiography,
                    view.tvKnownFor
                )
            }
        }
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
    private fun observePersonData(){
        viewModel.personDetails.observe(viewLifecycleOwner) {responseResource->
            when (responseResource) {
                is Resource.Success -> {
                    responseResource.data?.let { personDetailsResponse ->
                        val person = personDetailsResponse.toPerson()
                        setViews(person)
                        setColors(person, binding)
                        setupAdapter(movieCreditAdapter, movieGenreList)
                        setupRecyclerViewer(movieCreditAdapter, binding.rvMovies)
                        setupAdapter(serieCreditAdapter, serieGenreList)
                        setupRecyclerViewer(serieCreditAdapter, binding.rvSeries)
                        PagerSnapHelper().attachToRecyclerView(binding.rvMovies)
                        PagerSnapHelper().attachToRecyclerView(binding.rvSeries)
                        setToolbar()
                    }
                }

                is Resource.Error -> {
                    responseResource.message?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            "An error occurred: $message",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                else -> {}
            }
        }
    }
    private fun observeMovieData(id:Int) {

        mediaViewModel.movieGenre.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curGenreList = resourceResponse.data?.genres?.map { it.toMediaGenre() }
                    if (curGenreList != null) {
                        movieGenreList.addAll(curGenreList.toMutableList())
                        viewModel.getPersonMovieCredits(id)
                        observeMovieCredits()
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
    private fun observeSerieData(id:Int) {
        mediaViewModel.seriesGenre.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curGenreList = resourceResponse.data?.genres?.map { it.toMediaGenre() }
                    if (curGenreList != null) {
                        serieGenreList.addAll(curGenreList.toMutableList())
                        viewModel.getPersonSerieCredits(id)
                        observeSerieCredits()
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

    private fun observeMovieCredits(){
        viewModel.personMovieCredits.observe(viewLifecycleOwner){resourceResponse->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curlist = resourceResponse.data?.cast?.map { it.toPersonMediaCast() }
                    if (curlist != null) {
                        movieCreditList.addAll(curlist.toMutableList())
                        movieCreditAdapter.differ.submitList(movieCreditList)
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

    private fun observeSerieCredits(){
        viewModel.personSerieCredits.observe(viewLifecycleOwner){resourceResponse->
            when (resourceResponse) {
                is Resource.Success -> {
                    val curlist = resourceResponse.data?.cast?.map { it.toPersonMediaCast() }
                    if (curlist != null) {
                        serieCreditList.addAll(curlist.toMutableList())
                        serieCreditAdapter.differ.submitList(serieCreditList)
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

    private fun setupAdapter(adapter: MediaAdapter<PersonMediaCast>, genreList: MutableList<MediaGenre>){
        adapter.expressionOnCreateViewHolder = { viewGroup ->
            ActorMediaBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        adapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as ActorMediaBinding
            view.tvMovieName.text = eachItem.name
            view.tvRate.text = eachItem.popularity.toString()
            view.tvGenre.text = setCreditGenres(eachItem, genreList)

            if (eachItem.poster_path != null){
                setImage(view.ImgMovie, eachItem.poster_path, this.requireContext())
                CoroutineScope(Dispatchers.IO).launch {
                    eachItem.poster_path?.let {
                        view.mediaCardView.assignColors(
                            requireContext(),
                            it,
                            glide(requireContext()),
                            view.tvRate,
                            view.tvGenre,
                            view.tvMovieName,

                        )
                    }
                }
            }
            else view.ImgMovie.setImageResource(R.drawable.ic_baseline_people_alt_24)


            view.root.setOnClickListener {
                val action = R.id.action_personDetails_to_mediaDetailsFragment
                val bundle = Bundle().apply {
                    putInt("id", eachItem.id)
                    putString("mediaType", eachItem.type)

                }
                findNavController().navigate(action, bundle)
            }
        }
    }

    private fun setupRecyclerViewer(thisadapter: MediaAdapter<PersonMediaCast>,recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = thisadapter
        }
    }

    private fun setCreditGenres(item:PersonMediaCast,genreList:List<MediaGenre>?):String {
        val curGenreList: MutableList<String>? = mutableListOf<String>()
        var genres:String?=null
        for(item in item.genreIds)
            genreList?.filter { it.id == item }?.get(0)?.let { curGenreList?.add (it.name) }
        genres= curGenreList?.joinToString(",")
        return genres!!
    }


}