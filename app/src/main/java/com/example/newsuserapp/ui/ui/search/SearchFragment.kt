package com.example.newsuserapp.ui.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.core.widget.addTextChangedListener
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsuserapp.adapter.SearchNewsAdapter
import com.example.newsuserapp.databinding.FragmentSearchBinding
import com.example.newsuserapp.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.paging.filter
import com.example.newsuserapp.R
import com.example.newsuserapp.repository.NewsPagingSource
import com.example.newsuserapp.viewmodel.DeletedArticlesManager
import kotlinx.coroutines.Job

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var adapter: SearchNewsAdapter

    private var searchQuery: String? = null
    private var newsJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchNewsAdapter(
            onItemDeleted = { article ->
                viewModel.deleteArticle(article)
                fetchNews()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter



        val itemTouchHelper = adapter.getItemTouchHelper()
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        fetchNews()

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                fetchNews()
            }
            override fun afterTextChanged(s: Editable?) {}
        })



        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchNews()
        }
    }

    private fun fetchNews() {
        newsJob?.cancel()
        newsJob = viewLifecycleOwner.lifecycleScope.launch {
            val deletedArticles = DeletedArticlesManager.getDeletedUrls(requireContext())

            val pager = Pager(PagingConfig(pageSize = 10)) {
                NewsPagingSource(viewModel.repository.newsApi, searchQuery.orEmpty(), deletedArticles)
            }

            pager.flow.cachedIn(viewLifecycleOwner.lifecycleScope).collectLatest { pagingData ->
                val filteredData = pagingData.filter { item ->
                    val titleMatchesQuery = searchQuery.isNullOrBlank() ||
                            item.title?.contains(searchQuery!!, ignoreCase = true) == true

                    titleMatchesQuery &&
                            !item.title.isNullOrBlank() &&
                            !item.description.isNullOrBlank() &&
                            !item.urlToImage.isNullOrBlank() &&
                            !deletedArticles.contains(item.url)
                }

                adapter.submitData(filteredData)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        fetchNews()
    }
}
