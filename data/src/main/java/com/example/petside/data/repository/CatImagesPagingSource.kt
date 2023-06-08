package com.example.petside.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.domain.model.CatImage
import com.example.petside.data.retrofit.RetrofitService

class CatImagesPagingSource(
    val retrofitService: RetrofitService, val apiKey: String
) : PagingSource<Int, CatImage>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CatImage> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 0
            val response = retrofitService.getCatImages(apiKey, nextPageNumber)
            LoadResult.Page(
                data = response, prevKey = null, // Only paging forward.
                nextKey = if (response.size != 10) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CatImage>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
