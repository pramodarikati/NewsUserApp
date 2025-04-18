package com.example.newsuserapp.adapter

import android.content.Intent
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsuserapp.R
import com.example.newsuserapp.data.Article
import com.example.newsuserapp.databinding.ItemNewsFullBinding
import com.example.newsuserapp.ui.ui.NewsDetailActivity

class NewsPagingAdapter : PagingDataAdapter<Article, NewsPagingAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.url == newItem.url
            override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
        }
    }

    inner class NewsViewHolder(private val binding: ItemNewsFullBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.tvTitle.text = article.title
            binding.tvContent.text = article.description

            Glide.with(binding.ivNews.context)
                .load(article.urlToImage)
                .into(binding.ivNews)

            binding.root.alpha = 0f
            binding.root.animate()
                .alpha(1f)
                .setDuration(500)
                .start()

            val gestureDetector = GestureDetectorCompat(binding.root.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val deltaX = e2?.x?.minus(e1!!.x) ?: 0f
                    val deltaY = e2?.y?.minus(e1!!.y) ?: 0f

                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX > 100) {
                            val context = binding.root.context
                            val intent = Intent(context, NewsDetailActivity::class.java).apply {
                                putExtra("NEWS_URL", article.url)
                            }
                            context.startActivity(intent)
                        } else if (deltaX < -100) {
                            Toast.makeText(binding.root.context, "Skipped: ${article.title}", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                    return false
                }
            })

            binding.root.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
