package com.example.movieapplication.data.network.helper

import com.example.movieapplication.data.network.model.PersonDetails.PersonDetailsResponse
import com.example.movieapplication.data.network.model.moviecredits.MovieCreditResponse
import com.example.movieapplication.data.network.model.moviegenre.MovieGenreResponse
import com.example.movieapplication.data.network.model.moviedetails.MovieDetailsResponse
import com.example.movieapplication.data.network.model.seriegenre.SerieGenreResponse
import com.example.movieapplication.data.network.model.seriesdetails.SeriesDetails
import com.example.movieapplication.data.network.model.movies.MovieResponse
import com.example.movieapplication.data.network.model.people.PeopleResponse
import com.example.movieapplication.data.network.model.personmoviecredits.PersonMovieCreditResponse
import com.example.movieapplication.data.network.model.persontvcredits.PersonSerieCreditResponse
import com.example.movieapplication.data.network.model.series.SeriesResponse
import com.example.movieapplication.data.network.model.seriescredits.SeriesCreditResponse
import com.example.movieapplication.utility.APIKey.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/top_rated")
    suspend fun getTopMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
    ): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<MovieGenreResponse>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
    ): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMoviedetails(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<MovieDetailsResponse>

    @GET("tv/top_rated")
    suspend fun getTopSeries(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
    ): Response<SeriesResponse>

    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
    ): Response<SeriesResponse>

    @GET("tv/{tv_id}}")
    suspend fun getSeriedetails(
        @Path("tv_id") movie_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<SeriesDetails>

    @GET("genre/tv/list")
    suspend fun getSerieGenres(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<SerieGenreResponse>

    @GET("trending/person/day")
    suspend fun getTredingPeople(
        @Query("api_key") api_key: String = API_KEY,
    ): Response<PeopleResponse>

    @GET("person/popular")
    suspend fun getPopularPeople(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
    ): Response<PeopleResponse>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<MovieCreditResponse>

    @GET("tv/{tv_id}/credits")
    suspend fun getSeriesCredits(
        @Path("tv_id") tv_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<SeriesCreditResponse>

    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") movie_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<PersonDetailsResponse>

    @GET("person/{person_id}/movie_credits")
    suspend fun getPersonMovieCredits(
        @Path("person_id") movie_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<PersonMovieCreditResponse>

    @GET("person/{person_id}/tv_credits")
    suspend fun getPersonSerieCredits(
        @Path("person_id") movie_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "en-US",
    ): Response<PersonSerieCreditResponse>
}