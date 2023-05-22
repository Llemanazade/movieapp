package com.example.movieapplication.base

import androidx.paging.PagingSource
import androidx.paging.PagingState

class BasePagingSource<V : Any>(
    private val totalPages: Int? = null,
    private val block: suspend (Int) -> List<V>
) : PagingSource<Int, V>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, V> {
        val page = params.key ?: 1
        return try {
            val response = block(page)
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (totalPages != null && page == totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, V>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
