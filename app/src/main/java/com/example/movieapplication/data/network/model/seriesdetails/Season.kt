package com.example.movieapplication.data.network.model.seriesdetails

data class Season(
    val air_date: String,
    val episode_count: Int,
    val id: Int,
    val name: String,
    val overview: String,
    val poster_path: Any,
    val season_number: Int
)