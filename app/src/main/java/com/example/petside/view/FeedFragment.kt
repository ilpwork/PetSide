package com.example.petside.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petside.R
import com.example.petside.app.App
import com.example.petside.model.CatImage
import com.example.petside.utils.EndlessRecyclerViewScrollListener
import com.example.petside.viewmodel.ImageFeedViewModel
import kotlinx.coroutines.launch


class FeedFragment : Fragment() {

    private val viewModel: ImageFeedViewModel by viewModels()
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var feedAdapter: MyFeedRecyclerViewAdapter = MyFeedRecyclerViewAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).appComponent.inject(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view: RecyclerView =
            inflater.inflate(R.layout.feed_item_list, container, false) as RecyclerView

        viewModel.getUser()
        feedAdapter.addViewModel(viewModel)

        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
        }

        setEndScrollListener(view)
        setObservers()

        return view
    }

    private fun setObservers() {
        viewModel.liveFeedList.observe(viewLifecycleOwner) {
            feedAdapter.addCatImages(it as ArrayList<CatImage>)
        }

        viewModel.user.observe(viewLifecycleOwner) {
            fun onSuccess() {
                viewModel.getNextPage(true)
            }
            viewModel.apiKey = it.api_key
            viewModel.initialize(::onSuccess)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.errorMessage !== null) {
                        findNavController().navigate(
                            TabBarFragmentDirections.actionTabBarFragmentToAlertFragment(
                                message = uiState.errorMessage
                            )
                        )
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    private fun setEndScrollListener(view: RecyclerView) {
        scrollListener =
            object : EndlessRecyclerViewScrollListener(view.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    viewModel.getNextPage(false)
                }
            }
        view.addOnScrollListener(scrollListener)
    }

}