package com.example.movieapplication.features.moviefeature.model

data class PersonMediaCast (
    val id: Int,
    val poster_path: String?,
    val name: String,
    val popularity: Double,
    val releaseDate: String,
    val type: String,
    val genreIds: List<Int>
        )