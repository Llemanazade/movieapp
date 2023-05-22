package com.example.movieapplication.features.moviefeature.view.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.movieapplication.R
import com.example.movieapplication.databinding.FragmentPeopleBinding
import com.example.movieapplication.databinding.PeopleListBinding
import com.example.movieapplication.features.moviefeature.model.People
import com.example.movieapplication.features.moviefeature.view.adapters.MediaAdapter
import com.example.movieapplication.features.moviefeature.view.adapters.MediaPagerAdapter
import com.example.movieapplication.features.moviefeature.viewmodel.PeopleViewModel
import com.example.movieapplication.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PeopleFragment : Fragment() {
    private lateinit var binding: FragmentPeopleBinding
    private val viewModel: PeopleViewModel by activityViewModels()
    private val trendingPeopleAdapter by lazy { MediaAdapter<People>() }
    private val popularPeopleAdapter by lazy { MediaPagerAdapter<People>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            observeData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPeopleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getTrendingPeople()
        super.onViewCreated(view, savedInstanceState)
        PagerSnapHelper().attachToRecyclerView(rvPopularPeople)
        setupTrendingPeopleAdapter()
        setupTrendingPeopleRecycleView()
        setupPopularPeopleAdapter()
        setupPopularPeopleRecyclerView()


    }

    private fun setupTrendingPeopleAdapter() {

        trendingPeopleAdapter.expressionOnCreateViewHolder = { viewGroup ->
            PeopleListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        trendingPeopleAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as PeopleListBinding
            view.tvPersonName.text = eachItem.name
            view.tvSpeciality.text = eachItem.speciality
            view.tvRate.text = eachItem.rate.toString()

            if (eachItem.profile_path != null) {
                setImage(view.ImgPerson, eachItem.profile_path, this.requireContext())
                setColors(eachItem, view)
            } else view.ImgPerson.setImageResource(R.drawable.ic_baseline_people_alt_24)




            view.root.setOnClickListener {
                val action = R.id.action_peopleFragment_to_personDetails
                val bundle = Bundle().apply {
                    putInt("id", eachItem.id)

                }
                findNavController().navigate(action, bundle)
            }
        }
    }

    private fun setupTrendingPeopleRecycleView() {
        binding.rvPopularPeople.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = trendingPeopleAdapter
        }
    }

    private fun observeData() {
        observeTrendingPeople()
        observePopularPeople()

    }


    private fun observeTrendingPeople() {
        viewModel.trendingPeople.observe(viewLifecycleOwner) { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    resourceResponse.data?.let { response ->
                        trendingPeopleAdapter.differ.submitList(response.toPeopleResponse().results.toMutableList())
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

    private fun setupPopularPeopleAdapter() {

        popularPeopleAdapter.expressionOnCreateViewHolder = { viewGroup ->
            PeopleListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        popularPeopleAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as PeopleListBinding
            view.tvPersonName.text = eachItem.name
            view.tvSpeciality.text = eachItem.speciality
            view.tvRate.text = eachItem.rate.toString()
            if (eachItem.profile_path != null) {
                setImage(view.ImgPerson, eachItem.profile_path, this.requireContext())
                setColors(eachItem, view)
            } else view.ImgPerson.setImageResource(R.drawable.ic_baseline_people_alt_24)

            view.root.setOnClickListener {
                val action = R.id.action_peopleFragment_to_personDetails
                val bundle = Bundle().apply {
                    putInt("id", eachItem.id)

                }
                findNavController().navigate(action, bundle)
            }
        }
    }

    private fun setupPopularPeopleRecyclerView() {
        binding.rvAllPeople.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popularPeopleAdapter
        }
    }

    private fun observePopularPeople() {
        lifecycleScope.launch {
            viewModel.getPopularPeople().observe(viewLifecycleOwner) {
                it?.let {
                    val mediaPagingData = it.map { result ->
                        result.toPeople()
                    }
                    popularPeopleAdapter.submitData(lifecycle, mediaPagingData)
                }
            }
        }
    }

    private fun setColors(item: People, view: PeopleListBinding) {
        CoroutineScope(Dispatchers.IO).launch {
            item.profile_path?.let {
                view.mediaCardView.assignColors(
                    requireContext(),
                    it,
                    glide(requireContext()),
                    view.tvPersonName,
                    view.tvSpeciality,
                    view.tvRate
                )
            }
        }
    }

}