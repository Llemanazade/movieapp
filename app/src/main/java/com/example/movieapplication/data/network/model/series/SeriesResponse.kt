package com.example.movieapplication.data.network.model.series

data class SeriesResponse(
    val page: Int,
    val results: List<SeriesResult>,
    val total_pages: Int,
    val total_results: Int
)