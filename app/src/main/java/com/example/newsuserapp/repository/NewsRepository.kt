package com.example.newsuserapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsuserapp.data.Article
import com.example.newsuserapp.data.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor() {

    internal val newsApi = RetrofitInstance.api

    suspend fun getTopHeadlinesPaged(page: Int, country: String = "in"): NewsResponse {
        return withContext(Dispatchers.IO) {
            val response = newsApi.getTopHeadlines(
                apiKey = "your_api_key",
                country = country,
                page = page,
                pageSize = 10
            )

            if (response.isSuccessful) {
                response.body() ?: throw Exception("Response body is null")
            } else {
                throw Exception("API error: ${response.code()} ${response.message()}")
            }
        }
    }

    suspend fun refreshNews() {
        val response = newsApi.getTopHeadlines("india", page = 1, pageSize = 20)
//        db.articleDao().clearAll()
//        db.articleDao().insertAll(response.articles)
    }

//    fun searchNews(query: String): Flow<PagingData<Article>> {
//        return Pager(
//            config = PagingConfig(pageSize = 10),
//            pagingSourceFactory = { NewsPagingSource(newsApi, query, _deletedArticles) }
//        ).flow
//    }



}
