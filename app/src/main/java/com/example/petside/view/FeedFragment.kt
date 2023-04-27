package com.example.petside.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petside.R
import com.example.petside.app.App
import com.example.petside.db.UserEntity
import com.example.petside.model.CatImage
import com.example.petside.utils.EndlessRecyclerViewScrollListener
import com.example.petside.viewmodel.ImageFeedViewModel
import javax.inject.Inject


class FeedFragment : Fragment() {

    @Inject
    lateinit var user: LiveData<UserEntity>

    @Inject
    lateinit var imageFeedViewModelFactory: ImageFeedViewModel.Factory
    private lateinit var viewModel: ImageFeedViewModel
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var feedAdapter: MyFeedRecyclerViewAdapter = MyFeedRecyclerViewAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App)
            .appComponent
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: RecyclerView =
            inflater.inflate(R.layout.feed_item_list, container, false) as RecyclerView

        if (!this::viewModel.isInitialized) {
            viewModel = imageFeedViewModelFactory.create(viewLifecycleOwner, parentFragmentManager)
        }

        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
        }

        scrollListener =
            object : EndlessRecyclerViewScrollListener(view.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    viewModel.getNextPage()
                }
            }
        view.addOnScrollListener(scrollListener)

        feedUpdateObserver()

        return view
    }

    private fun feedUpdateObserver() {
        viewModel.catImages.observe(viewLifecycleOwner) {
            feedAdapter.addCatImages(it as ArrayList<CatImage>)
        }
    }

}