package com.example.newsuserapp.ui.ui.home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.newsuserapp.R
import com.example.newsuserapp.adapter.NewsPagingAdapter
import com.example.newsuserapp.databinding.FragmentHomeBinding
import com.example.newsuserapp.viewmodel.NewsViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var adapter: NewsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NewsPagingAdapter()
        binding.viewPager.adapter = adapter
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)


        fetchNews()

        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        binding.viewPager.setPageTransformer { page, position ->
            page.alpha = 1 - kotlin.math.abs(position)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }


        loadProfile()
    }
    private fun fetchNews() {
        lifecycleScope.launch {
            viewModel.newsFlow.collectLatest {
                adapter.submitData(it)
                binding.swipeRefreshLayout.postDelayed({
                    binding.swipeRefreshLayout.isRefreshing = false
                }, 300)
            }
        }
    }


    private fun loadProfile() {
        val sharedPref = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val localUri = sharedPref.getString("profile_image", null)

        if (!localUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(localUri))
                .circleCrop()
                .into(binding.imageProfile)
        } else {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userName = user.displayName ?: "Guest"
                binding.name.text = "Hello, $userName"

                val firebaseUri = user.photoUrl
                if (firebaseUri != null) {
                    Glide.with(this)
                        .load(firebaseUri)
                        .circleCrop()
                        .into(binding.imageProfile)
                } else {
                    Glide.with(this)
                        .load(R.drawable.ic_profile)
                        .circleCrop()
                        .into(binding.imageProfile)
                }
            } else {
                Glide.with(this)
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.imageProfile)
                binding.name.text = "Hello, Guest"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
