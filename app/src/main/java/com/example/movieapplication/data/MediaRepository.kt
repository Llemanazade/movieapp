package com.example.movieapplication.data

import androidx.paging.liveData
import com.example.movieapplication.data.network.helper.ApiService
import com.example.movieapplication.data.network.model.PersonDetails.PersonDetailsResponse
import com.example.movieapplication.data.network.model.moviecredits.MovieCreditResponse
import com.example.movieapplication.data.network.model.moviedetails.MovieDetailsResponse
import com.example.movieapplication.data.network.model.moviegenre.MovieGenreResponse
import com.example.movieapplication.data.network.model.movies.MovieResponse
import com.example.movieapplication.data.network.model.people.PeopleResponse
import com.example.movieapplication.data.network.model.personmoviecredits.PersonMovieCreditResponse
import com.example.movieapplication.data.network.model.persontvcredits.PersonSerieCreditResponse
import com.example.movieapplication.data.network.model.seriegenre.SerieGenreResponse
import com.example.movieapplication.data.network.model.series.SeriesResponse
import com.example.movieapplication.data.network.model.seriescredits.SeriesCreditResponse
import com.example.movieapplication.data.network.model.seriesdetails.SeriesDetails
import com.example.movieapplication.features.moviefeature.view.fragments.PersonDetails
import com.example.movieapplication.utility.createPager
import retrofit2.Response

class MediaRepository(private val apiService: ApiService) {
     suspend fun getTopMovies() : Response<MovieResponse> = apiService.getTopMovies()

    suspend fun getMovieGenres() : Response<MovieGenreResponse> = apiService.getMovieGenres()

    fun getPopularMovies() = createPager { page ->
        apiService.getPopularMovies(page = page).body()?.results!!
    }.liveData

    suspend fun getMovieDetails(id:Int) : Response<MovieDetailsResponse> = apiService.getMoviedetails(id)


    suspend fun getTopSeries() : Response<SeriesResponse> = apiService.getTopSeries()

    fun getPopularSeries() = createPager { page ->
        apiService.getPopularSeries(page = page).body()?.results!!
    }.liveData

    suspend fun getSerieDetails(id:Int) : Response<SeriesDetails> = apiService.getSeriedetails(id)

    suspend fun getSerieGenres() : Response<SerieGenreResponse> = apiService.getSerieGenres()

    suspend fun getTrendingPeople() : Response<PeopleResponse> = apiService.getTredingPeople()

    fun getPopularPeople() = createPager { page ->
        apiService.getPopularPeople(page = page).body()?.results!!
    }.liveData

    suspend fun getMovieCredits(movieId:Int): Response<MovieCreditResponse> = apiService.getMovieCredits(movieId)

    suspend fun getSeriesCredits(tvId:Int): Response<SeriesCreditResponse> = apiService.getSeriesCredits(tvId)

    suspend fun getPersonDetails(id:Int): Response<PersonDetailsResponse> = apiService.getPersonDetails(id)

    suspend fun getPersonMovieCredits(id:Int): Response<PersonMovieCreditResponse> = apiService.getPersonMovieCredits(id)

    suspend fun getPersonSerieCredits(id:Int): Response<PersonSerieCreditResponse> = apiService.getPersonSerieCredits(id)



}