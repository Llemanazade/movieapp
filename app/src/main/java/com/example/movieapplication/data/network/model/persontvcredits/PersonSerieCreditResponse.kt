package com.example.movieapplication.data.network.model.persontvcredits

data class PersonSerieCreditResponse(
    val cast: List<PersonSerieCast>,
    val crew: List<Any>,
    val id: Int
)