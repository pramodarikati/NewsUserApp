package com.example.newsuserapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsuserapp.data.Article
import com.example.newsuserapp.data.NewsApi

class NewsPagingSource(
    private val newsApi: NewsApi,
    private val query: String,
    _deletedArticles: Set<String>
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 1
            val response = newsApi.getTopHeadlines(page = page)
            val articles = response.body()?.articles ?: emptyList()

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}


