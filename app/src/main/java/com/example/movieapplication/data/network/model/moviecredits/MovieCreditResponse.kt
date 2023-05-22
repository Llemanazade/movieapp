package com.example.movieapplication.data.network.model.moviecredits

data class MovieCreditResponse(
    val cast: List<MovieCast>,
    val crew: List<Crew>,
    val id: Int
)