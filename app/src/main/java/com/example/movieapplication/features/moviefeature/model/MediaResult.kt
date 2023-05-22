package com.example.movieapplication.features.moviefeature.model

data class MediaResult (
    val genreIds:List<Int>,
    val title:String,
    val id:Int,
    val original_language: String,
    val poster_path: String,
    val release_date: String,
    val vote_average: Double,
)



