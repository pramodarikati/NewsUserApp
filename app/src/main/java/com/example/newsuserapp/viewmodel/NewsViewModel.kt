package com.example.newsuserapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsuserapp.data.Article
import com.example.newsuserapp.repository.NewsPagingSource
import com.example.newsuserapp.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
     val repository: NewsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _deletedArticles = DeletedArticlesManager.getDeletedUrls(context)

    var query: String = ""

    val newsFlow: Flow<PagingData<Article>> = Pager(PagingConfig(pageSize = 10)) {
        NewsPagingSource(repository.newsApi, query, _deletedArticles)
    }.flow.cachedIn(viewModelScope)

    fun deleteArticle(article: Article) {
        DeletedArticlesManager.addDeletedUrl(context, article.url ?: "")
    }

}
