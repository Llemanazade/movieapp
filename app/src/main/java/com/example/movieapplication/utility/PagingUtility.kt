package com.example.movieapplication.utility

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.movieapplication.base.BasePagingSource
import com.example.movieapplication.utility.Constants.NETWORK_PAGE_SIZE

fun <V : Any> createPager(
    totalPages: Int? = null,
    pageSize: Int = NETWORK_PAGE_SIZE,
    enablePlaceholders: Boolean = false,
    block: suspend (Int) -> List<V>
): Pager<Int, V> = Pager(
    config = PagingConfig(enablePlaceholders = enablePlaceholders, pageSize = pageSize),
    pagingSourceFactory = { BasePagingSource(totalPages,block) }
)