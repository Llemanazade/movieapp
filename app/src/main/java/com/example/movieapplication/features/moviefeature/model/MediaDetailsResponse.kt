package com.example.movieapplication.features.moviefeature.model

data class MediaDetailsResponse (
    val id:Int,
    val title:String,
    val release_date:String,
    val original_title:String,
    val overview:String,
    val poster_path:String?,
    val backdrop_path:String?,
    val genres: List<MediaGenre>

)