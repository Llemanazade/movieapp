package com.example.movieapplication.features.moviefeature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapplication.data.MediaRepository
import com.example.movieapplication.data.network.model.PersonDetails.PersonDetailsResponse
import com.example.movieapplication.data.network.model.people.PeopleResponse
import com.example.movieapplication.data.network.model.people.PeopleResult
import com.example.movieapplication.data.network.model.personmoviecredits.PersonMovieCreditResponse
import com.example.movieapplication.data.network.model.persontvcredits.PersonSerieCreditResponse
import com.example.movieapplication.utility.InternetConnectivityObserver
import com.example.movieapplication.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val repository: MediaRepository,
    private val networkStatusChecker: InternetConnectivityObserver.NetworkStatusChecker,
) : ViewModel() {

    private val _trendingPeople = MutableLiveData<Resource<PeopleResponse>>()
    val trendingPeople: LiveData<Resource<PeopleResponse>> = _trendingPeople

    private val _personDetails = MutableLiveData<Resource<PersonDetailsResponse>>()
    val personDetails: LiveData<Resource<PersonDetailsResponse>> = _personDetails

    private val _personMovieCredits = MutableLiveData<Resource<PersonMovieCreditResponse>>()
    val personMovieCredits: LiveData<Resource<PersonMovieCreditResponse>> = _personMovieCredits

    private val _personSerieCredits = MutableLiveData<Resource<PersonSerieCreditResponse>>()
    val personSerieCredits: LiveData<Resource<PersonSerieCreditResponse>> = _personSerieCredits

    fun getPopularPeople(): LiveData<PagingData<PeopleResult>> {
        return repository.getPopularPeople().cachedIn(viewModelScope)
    }

    fun getTrendingPeople() = viewModelScope.launch {
        _trendingPeople.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getTrendingPeople()
                _trendingPeople.postValue(handleTrendingPeople(response))
            } else _trendingPeople.postValue(Resource.Error("No Internet connection"))

        } catch (error: Throwable) {
            when (error) {
                is IOException -> _trendingPeople.postValue(Resource.Error("Network failure"))
                else -> _trendingPeople.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getPersonDetails(id: Int) = viewModelScope.launch {
        _personDetails.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getPersonDetails(id)
                _personDetails.postValue(handlePersonDetails(response))
            } else _personDetails.postValue(Resource.Error("No Internet connection"))

        } catch (error: Throwable) {
            when (error) {
                is IOException -> _personDetails.postValue(Resource.Error("Network failure"))
                else -> _personDetails.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getPersonMovieCredits(id: Int) = viewModelScope.launch {
        _personMovieCredits.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getPersonMovieCredits(id)
                _personMovieCredits.postValue(handlePersonMovieCredit(response))
            } else _personMovieCredits.postValue(Resource.Error("No Internet connection"))

        } catch (error: Throwable) {
            when (error) {
                is IOException -> _personMovieCredits.postValue(Resource.Error("Network failure"))
                else -> _personMovieCredits.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getPersonSerieCredits(id: Int) = viewModelScope.launch {
        _personSerieCredits.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getPersonSerieCredits(id)
                _personSerieCredits.postValue(handlePersonSerieCredit(response))
            } else _personSerieCredits.postValue(Resource.Error("No Internet connection"))

        } catch (error: Throwable) {
            when (error) {
                is IOException -> _personSerieCredits.postValue(Resource.Error("Network failure"))
                else -> _personSerieCredits.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleTrendingPeople(response: Response<PeopleResponse>): Resource<PeopleResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePersonDetails(response: Response<PersonDetailsResponse>): Resource<PersonDetailsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePersonMovieCredit(response: Response<PersonMovieCreditResponse>): Resource<PersonMovieCreditResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePersonSerieCredit(response: Response<PersonSerieCreditResponse>): Resource<PersonSerieCreditResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}