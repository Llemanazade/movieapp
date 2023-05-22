package com.example.movieapplication.features.moviefeature.model

data class MediaResponse (
    var results:List<MediaResult>,
    var page:Int,
    var total_pages:Int,
    var total_results:Int
)