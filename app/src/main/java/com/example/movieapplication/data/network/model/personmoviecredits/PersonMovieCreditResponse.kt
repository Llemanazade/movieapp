package com.example.movieapplication.data.network.model.personmoviecredits

data class PersonMovieCreditResponse(
    val cast: List<PersonMovieCast>,
    val crew: List<Crew>,
    val id: Int
)