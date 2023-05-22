package com.example.movieapplication.utility

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.movieapplication.R
import com.example.movieapplication.data.network.model.PersonDetails.PersonDetailsResponse
import com.example.movieapplication.data.network.model.moviecredits.MovieCast
import com.example.movieapplication.data.network.model.moviecredits.MovieCreditResponse
import com.example.movieapplication.data.network.model.moviedetails.MovieDetailsResponse
import com.example.movieapplication.data.network.model.moviegenre.MovieGenre
import com.example.movieapplication.data.network.model.moviegenre.MovieGenreResponse
import com.example.movieapplication.data.network.model.movies.MovieResponse
import com.example.movieapplication.data.network.model.movies.MovieResult
import com.example.movieapplication.data.network.model.people.PeopleResponse
import com.example.movieapplication.data.network.model.people.PeopleResult
import com.example.movieapplication.data.network.model.personmoviecredits.PersonMovieCast
import com.example.movieapplication.data.network.model.personmoviecredits.PersonMovieCreditResponse
import com.example.movieapplication.data.network.model.persontvcredits.PersonSerieCast
import com.example.movieapplication.data.network.model.persontvcredits.PersonSerieCreditResponse
import com.example.movieapplication.data.network.model.seriegenre.SerieGenre
import com.example.movieapplication.data.network.model.seriegenre.SerieGenreResponse
import com.example.movieapplication.data.network.model.series.SeriesResponse
import com.example.movieapplication.data.network.model.series.SeriesResult
import com.example.movieapplication.data.network.model.seriescredits.SeriesCast
import com.example.movieapplication.data.network.model.seriescredits.SeriesCreditResponse
import com.example.movieapplication.data.network.model.seriesdetails.SeriesDetails
import com.example.movieapplication.features.moviefeature.model.*
import com.example.movieapplication.utility.Constants.IMAGE_BASE_URL

fun MovieResult.toResult() = MediaResult(
    id = id,
    genreIds = genre_ids,
    title = title,
    original_language = original_language,
    vote_average = vote_average,
    release_date = release_date,
    poster_path = String.format(IMAGE_BASE_URL, poster_path)
)

fun MovieResponse.toResponse() = MediaResponse(
    results = results.map { it.toResult() },
    page = page,
    total_pages = total_pages,
    total_results = total_results
)

fun SeriesResult.toResult() = MediaResult(
    id = id,
    genreIds = genre_ids,
    title = name,
    original_language = original_language,
    vote_average = vote_average,
    release_date = first_air_date,
    poster_path = String.format(IMAGE_BASE_URL, poster_path)
)

fun SeriesResponse.toResponse() = MediaResponse(
    results = results.map { it.toResult() },
    page = page,
    total_pages = total_pages,
    total_results = total_results
)

fun MovieGenre.toMediaGenre() = MediaGenre(
    id = id,
    name = name
)

fun MovieGenreResponse.toGenreResponse() = MediaGenreResponse(
    genres = genres.map { it.toMediaGenre() }
)

fun SerieGenre.toMediaGenre() = MediaGenre(
    id = id,
    name = name
)

fun SerieGenreResponse.toGenreResponse() = MediaGenreResponse(
    genres = genres.map { it.toMediaGenre() }
)

fun MovieDetailsResponse.toMediaDetails() = MediaDetailsResponse(
    id = id,
    title = title,
    release_date = release_date,
    overview = overview,
    poster_path = if (poster_path == null) null else String.format(IMAGE_BASE_URL, poster_path),
    backdrop_path = String.format(IMAGE_BASE_URL, poster_path),
    genres = genres.map { it.toMediaGenre() },
    original_title = original_title
)

fun SeriesDetails.toMediaDetails() = MediaDetailsResponse(
    id = id,
    title = name,
    release_date = first_air_date,
    overview = overview,
    poster_path = if (poster_path == null) null else String.format(IMAGE_BASE_URL, poster_path),
    backdrop_path = String.format(IMAGE_BASE_URL, poster_path),
    genres = genres.map { it.toMediaGenre() },
    original_title = original_name
)

fun PeopleResponse.toPeopleResponse() = TrendingPeople(
    id = page,
    results = results.map { it.toPeople() }

)

fun PeopleResult.toPeople() = People(
    id = id,
    name = name,
    speciality = known_for_department,
    rate = popularity,
    profile_path = if (profile_path == null) null else String.format(IMAGE_BASE_URL, profile_path)
)

fun MovieCast.toMediaCast() = MediaCast(
    Id = id,
    Name = name,
    Character = character,
    rate = popularity,
    profile = if (profile_path == null) null else String.format(IMAGE_BASE_URL, profile_path)
)

fun MovieCreditResponse.toMediaCreditresponse() = MediaCreditResponse(
    id = id,
    cast = cast.map { it.toMediaCast() }
)

fun SeriesCast.toMediaCast() = MediaCast(
    Id = id,
    Name = name,
    Character = character,
    profile = if (profile_path == null) null else String.format(IMAGE_BASE_URL, profile_path),
    rate = popularity

)

fun SeriesCreditResponse.toMediaCreditresponse() = MediaCreditResponse(
    id = id,
    cast = cast.map { it.toMediaCast() }
)

fun PersonDetailsResponse.toPerson() = Person(
    id = id,
    name = name,
    gender = gender,
    biography = biography,
    birthDate = birthday,
    profilePath = if (profile_path == null) null else String.format(IMAGE_BASE_URL, profile_path),
    rate = popularity

)

fun PersonMovieCast.toPersonMediaCast() = PersonMediaCast(
    id = id,
    name = title,
    releaseDate = release_date,
    popularity = popularity,
    poster_path = if (poster_path == null) null else String.format(IMAGE_BASE_URL, poster_path),
    type = "Movies",
    genreIds = genre_ids
)

fun PersonMovieCreditResponse.toPersonMediaCreditResponse() = PersonMediaResponse(
    id = id,
    cast = cast.map { it.toPersonMediaCast() }
)

fun PersonSerieCast.toPersonMediaCast() = PersonMediaCast(
    id = id,
    name = name,
    releaseDate = first_air_date,
    popularity = popularity,
    poster_path = if (poster_path == null) null else String.format(IMAGE_BASE_URL, poster_path),
    type = "Series",
    genreIds = genre_ids
)

fun PersonSerieCreditResponse.toPersonMediaCreditResponse() = PersonMediaResponse(
    id = id,
    cast = cast.map { it.toPersonMediaCast() }
)

fun glide(context: Context) = Glide.with(context).setDefaultRequestOptions(
    RequestOptions()
        .placeholder(R.drawable.ic_baseline_movie_24)
        .error(R.drawable.ic_baseline_local_movies_24)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
)

fun setImage(imageView: ImageView, posterpath: String, context: Context) =
    glide(context)
        .load(posterpath)
        .into(imageView)

fun setGenres(result: MediaResult, genreList: List<MediaGenre>?): String {
    val curGenreList: MutableList<String>? = mutableListOf<String>()
    var genres: String? = null
    for (item in result.genreIds)
        genreList?.filter { it.id == item }?.get(0)?.let { curGenreList?.add(it.name) }
    genres = curGenreList?.joinToString(",")
    return genres!!
}

fun setDetailsGenre(mediadetail: MediaDetailsResponse): String {
    var curGenreList: MutableList<String>? = mutableListOf<String>()
    var genres: String? = null
    for (item in mediadetail.genres)
        curGenreList?.add(item.name)
    genres = curGenreList?.joinToString(",")
    return genres!!
}

fun View.assignColors(
    context: Context,
    imageLink: String,
    glide: RequestManager,
    vararg textView: TextView,
) {
    val bitmap = glide.asBitmap().load(imageLink).submit().get()
    Palette.from(bitmap).generate {
        it?.let {
            val backgroundColor = it.getDominantColor(
                ContextCompat.getColor(
                    context,
                    androidx.appcompat.R.color.background_material_dark
                )
            )

            when (this) {
                is CardView -> setCardBackgroundColor(backgroundColor)
                else -> setBackgroundColor(backgroundColor)
            }

            textView.forEach { textView ->
                textView.setTextColorAgainstBackgroundColor(backgroundColor)
            }
        }
    }
}

fun TextView.setTextColorAgainstBackgroundColor(backgroundColor: Int) {
    val textColor = if (backgroundColor.isDark()) Color.WHITE else Color.BLACK
    this.setTextColor(textColor)
}

fun Int.isDark(): Boolean {
    val darkness = 1 - (0.299 * Color.red(this) +
            0.587 * Color.green(this) +
            0.114 * Color.blue(this)) / 255
    return darkness >= 0.5
}



