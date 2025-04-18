package com.example.newsuserapp.adapter

import android.content.Intent
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.bumptech.glide.Glide
import com.example.newsuserapp.R
import com.example.newsuserapp.data.Article
import com.example.newsuserapp.databinding.ItemNewsCardBinding
import com.example.newsuserapp.ui.MainActivity
import com.example.newsuserapp.ui.ui.NewsDetailActivity
import com.example.newsuserapp.ui.ui.search.SearchFragment
import com.example.newsuserapp.viewmodel.DeletedArticlesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchNewsAdapter(private val onItemDeleted: (Article) -> Unit) :
    PagingDataAdapter<Article, SearchNewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.url == newItem.url
            override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
        }
    }

    inner class NewsViewHolder(private val binding: ItemNewsCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            if (DeletedArticlesManager.getDeletedUrls(binding.root.context).contains(article.url)) return

            binding.textTitle.text = article.title
            binding.textDescription.text = article.description

            Glide.with(binding.imageNews.context)
                .load(article.urlToImage)
                .into(binding.imageNews)

            binding.root.alpha = 0f
            binding.root.animate().alpha(1f).setDuration(500).start()

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, NewsDetailActivity::class.java).apply {
                    putExtra("NEWS_URL", article.url)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { item ->
            if (!DeletedArticlesManager.getDeletedUrls(holder.itemView.context).contains(item.url)) {
                holder.bind(item)
            }
        }
    }

    fun getItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val article = getItem(position)

                if (direction == ItemTouchHelper.LEFT) {
                    article?.url?.let { url ->
                        DeletedArticlesManager.addDeletedUrl(viewHolder.itemView.context, url)
                        showSnackbar(viewHolder.itemView, "Deleted: ${article.title}")
                        onItemDeleted(article)

//                        notifyItemRemoved(position)
                    }
                }
                else if (direction == ItemTouchHelper.RIGHT) {
                    val context = viewHolder.itemView.context
                    val intent = Intent(context, NewsDetailActivity::class.java).apply {
                        putExtra("NEWS_URL", article?.url)
                    }
                    context.startActivity(intent)

                    showSnackbar(viewHolder.itemView, "Opening details for: ${article?.title}")
                }
            }


            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
    }

    private fun showSnackbar(view: View, message: String) {
        com.google.android.material.snackbar.Snackbar.make(view, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
    }
}
