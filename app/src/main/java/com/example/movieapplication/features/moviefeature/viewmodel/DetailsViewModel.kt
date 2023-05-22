package com.example.movieapplication.features.moviefeature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapplication.data.MediaRepository
import com.example.movieapplication.data.network.model.moviecredits.MovieCreditResponse
import com.example.movieapplication.data.network.model.moviedetails.MovieDetailsResponse
import com.example.movieapplication.data.network.model.seriescredits.SeriesCreditResponse
import com.example.movieapplication.data.network.model.seriesdetails.SeriesDetails
import com.example.movieapplication.utility.InternetConnectivityObserver
import com.example.movieapplication.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MediaRepository,
    private val networkStatusChecker: InternetConnectivityObserver.NetworkStatusChecker,
) : ViewModel() {

    private var _movieDetail = MutableLiveData<Resource<MovieDetailsResponse>>()
    val movieDetail: LiveData<Resource<MovieDetailsResponse>>? = _movieDetail

    private var _seriesDetail = MutableLiveData<Resource<SeriesDetails>>()
    val seriesDetail: LiveData<Resource<SeriesDetails>>? = _seriesDetail


    private val _movieCredit = MutableLiveData<Resource<MovieCreditResponse>>()
    val movieCredit: LiveData<Resource<MovieCreditResponse>> = _movieCredit

    private val _seriesCredit = MutableLiveData<Resource<SeriesCreditResponse>>()
    val seriesCredit: LiveData<Resource<SeriesCreditResponse>> = _seriesCredit

    fun getMovieDetails(id: Int) = viewModelScope.launch {
        _movieDetail.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getMovieDetails(id)
                _movieDetail.postValue(handleMovieDetailsResponse(response))
            } else {
                _movieDetail.postValue(Resource.Error("No internet connection"))
            }
        } catch (error: Throwable) {
            when (error) {
                is IOException -> _movieDetail.postValue(Resource.Error("Network Failure"))
                else -> _movieDetail.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getSerieDetails(id: Int) = viewModelScope.launch {
        _seriesDetail.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getSerieDetails(id)
                _seriesDetail.postValue(handleSeriesDetailsResponse(response))
            } else {
                _seriesDetail.postValue(Resource.Error("No internet connection"))
            }
        } catch (error: Throwable) {
            when (error) {
                is IOException -> _seriesDetail.postValue(Resource.Error("Network Failure"))
                else -> _seriesDetail.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getMovieCredits(id: Int) = viewModelScope.launch {

        _movieCredit.postValue(Resource.Loading())
        try {

            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getMovieCredits(id)
                _movieCredit.postValue(handleMovieCredits(response))
            } else _movieCredit.postValue(Resource.Error("No Internet Connection"))
        } catch (error: Throwable) {
            when (error) {
                is IOException -> _movieCredit.postValue(Resource.Error("Network failure"))
                else -> _movieCredit.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getSeriesCredits(id: Int) = viewModelScope.launch {

        _seriesCredit.postValue(Resource.Loading())
        try {

            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getSeriesCredits(id)
                _seriesCredit.postValue(handleSeriesCredits(response))
            } else _seriesCredit.postValue(Resource.Error("No Internet Connection"))
        } catch (error: Throwable) {
            when (error) {
                is IOException -> _seriesCredit.postValue(Resource.Error("Network failure"))
                else -> _seriesCredit.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleMovieDetailsResponse(response: Response<MovieDetailsResponse>): Resource<MovieDetailsResponse> {
        if (response.isSuccessful) {
            response.body().let { responseResult ->
                return Resource.Success(responseResult!!)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSeriesDetailsResponse(response: Response<SeriesDetails>): Resource<SeriesDetails> {
        if (response.isSuccessful) {
            response.body().let { responseResult ->
                return Resource.Success(responseResult!!)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleMovieCredits(response: Response<MovieCreditResponse>): Resource<MovieCreditResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSeriesCredits(response: Response<SeriesCreditResponse>): Resource<SeriesCreditResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}

