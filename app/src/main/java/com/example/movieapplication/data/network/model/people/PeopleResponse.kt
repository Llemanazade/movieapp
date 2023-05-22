package com.example.movieapplication.data.network.model.people

data class PeopleResponse(
    val page: Int,
    val results: List<PeopleResult>,
    val total_pages: Int,
    val total_results: Int
)