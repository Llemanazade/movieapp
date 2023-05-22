package com.example.movieapplication.data.network.model.seriescredits

data class SeriesCreditResponse(
    val cast: List<SeriesCast>,
    val crew: List<Crew>,
    val id: Int
)