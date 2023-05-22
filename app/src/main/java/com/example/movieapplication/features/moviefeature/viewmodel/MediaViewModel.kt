package com.example.movieapplication.features.moviefeature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapplication.data.MediaRepository
import com.example.movieapplication.data.network.model.moviegenre.MovieGenreResponse
import com.example.movieapplication.data.network.model.movies.MovieResponse
import com.example.movieapplication.data.network.model.movies.MovieResult
import com.example.movieapplication.data.network.model.seriegenre.SerieGenreResponse
import com.example.movieapplication.data.network.model.series.SeriesResponse
import com.example.movieapplication.data.network.model.series.SeriesResult
import com.example.movieapplication.utility.InternetConnectivityObserver
import com.example.movieapplication.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val repository: MediaRepository,
    private val networkStatusChecker: InternetConnectivityObserver.NetworkStatusChecker,
) : ViewModel() {

    //Movies

    private val _topMovies = MutableLiveData<Resource<MovieResponse>>()
    val topMovies: LiveData<Resource<MovieResponse>> = _topMovies

    private val _movieGenre = MutableLiveData<Resource<MovieGenreResponse>>()
    val movieGenre: LiveData<Resource<MovieGenreResponse>> = _movieGenre


    private val _topSeries = MutableLiveData<Resource<SeriesResponse>>()
    val topSeries: LiveData<Resource<SeriesResponse>> = _topSeries

    private val _seriesGenre = MutableLiveData<Resource<SerieGenreResponse>>()
    val seriesGenre: LiveData<Resource<SerieGenreResponse>> = _seriesGenre


    fun getPopularSeries(): LiveData<PagingData<SeriesResult>> {
        return repository.getPopularSeries().cachedIn(viewModelScope)
    }

    fun getPopularMovies(): LiveData<PagingData<MovieResult>> {
        return repository.getPopularMovies().cachedIn(viewModelScope)
    }

    fun getTopRatedMovies() = viewModelScope.launch {
        _topMovies.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getTopMovies()
                _topMovies.postValue(handleMovieResponse(response))
            } else _topMovies.postValue(Resource.Error("No Internet connection"))

        } catch (error: Throwable) {
            when (error) {
                is IOException -> _topMovies.postValue(Resource.Error("Network failure"))
                else -> _topMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getTopRatedSeries() = viewModelScope.launch {
        _topSeries.postValue(Resource.Loading())
        try {
            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getTopSeries()
                _topSeries.postValue(handleSeriesResponse(response))
            } else _topSeries.postValue(Resource.Error("No Internet connection"))

        } catch (error: Throwable) {
            when (error) {
                is IOException -> _topSeries.postValue(Resource.Error("Network failure"))
                else -> _topSeries.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getMovieGenres() = viewModelScope.launch {
        _movieGenre.postValue(Resource.Loading())
        try {

            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getMovieGenres()
                _movieGenre.postValue(handleMovieGenreResponse(response))
            } else _movieGenre.postValue(Resource.Error("No Internet Connection"))
        } catch (error: Throwable) {
            when (error) {
                is IOException -> _movieGenre.postValue(Resource.Error("Network failure"))
                else -> _movieGenre.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getSeriesGenres() = viewModelScope.launch {
        _seriesGenre.postValue(Resource.Loading())
        try {

            if (networkStatusChecker.hasInternetConnection()) {
                val response = repository.getSerieGenres()
                _seriesGenre.postValue(handleSeriesGenreResponse(response))
            } else _seriesGenre.postValue(Resource.Error("No Internet Connection"))
        } catch (error: Throwable) {
            when (error) {
                is IOException -> _seriesGenre.postValue(Resource.Error("Network failure"))
                else -> _seriesGenre.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun handleMovieResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleMovieGenreResponse(response: Response<MovieGenreResponse>): Resource<MovieGenreResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSeriesResponse(response: Response<SeriesResponse>): Resource<SeriesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSeriesGenreResponse(response: Response<SerieGenreResponse>): Resource<SerieGenreResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}


