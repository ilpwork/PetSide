package com.example.petside.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petside.R
import com.example.petside.app.App
import com.example.petside.db.UserEntity
import com.example.petside.model.CatImage
import com.example.petside.retrofit.RetrofitService
import com.example.petside.utils.EndlessRecyclerViewScrollListener
import com.example.petside.viewmodel.ImageFeedViewModel
import retrofit2.HttpException
import javax.inject.Inject


class FeedFragment : Fragment() {

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var user: LiveData<UserEntity>

    private val viewModel: ImageFeedViewModel by activityViewModels()
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

        viewModel.retrofitService = retrofitService
        feedAdapter.addViewModel(viewModel)

        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = feedAdapter
        }

        createEndScrollListener(view)
        feedUpdateObserver()
        userObserver()

        return view
    }

    private fun feedUpdateObserver() {
        viewModel.liveFeedList.observe(viewLifecycleOwner) {
            feedAdapter.addCatImages(it as ArrayList<CatImage>)
        }
    }

    private fun userObserver() {
        fun onSuccess() {
            viewModel.getNextPage(true, ::onError)
        }
        user.observe(viewLifecycleOwner) {
            viewModel.initialize(it.api_key, ::onSuccess, ::onError)
        }
    }

    private fun createEndScrollListener(view: RecyclerView) {
        scrollListener =
            object : EndlessRecyclerViewScrollListener(view.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    viewModel.getNextPage(false, ::onError)
                }
            }
        view.addOnScrollListener(scrollListener)
    }

    fun onError(e: HttpException) {
        val dialog = AlertFragment(e.message())
        dialog.show(parentFragmentManager, "addToFavouritesError")
    }

}