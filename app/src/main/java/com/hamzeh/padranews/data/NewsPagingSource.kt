package com.hamzeh.padranews.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hamzeh.padranews.api.NewsService
import com.hamzeh.padranews.data.NewsRepository.Companion.NETWORK_PAGE_SIZE
import com.hamzeh.padranews.model.News
import okio.IOException
import retrofit2.HttpException

// newsApi page API is 1 based
private const val NEWSAPI_STARTING_PAGE_INDEX = 1

class NewsPagingSource(
    private val service: NewsService,
    private val query: String
) : PagingSource<Int, News>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        val position = params.key ?: NEWSAPI_STARTING_PAGE_INDEX
        val apiQuery = query
        Log.d("pagingSource", "loadSize: ${params.loadSize}")
        return try {
            val response = service.getHeadLines(page = position, itemsPerPage = params.loadSize)
            val newsList = response.articles
            Log.d("pagingSource", "responseSize: ${newsList.size}")
            val nextKey = if (newsList.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = newsList,
                prevKey = if (position == NEWSAPI_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            Log.d("pagingSource", "ioexception: ${exception.message}")

            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d("pagingSource", "httpexception: ${exception.message}")
            LoadResult.Error(exception)
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, News>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        Log.d("pagingSource", "anchorPosition: ${state.anchorPosition}")
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}