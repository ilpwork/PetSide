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
    private var catImages = ArrayList<CatImage>()
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

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

        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = MyFeedRecyclerViewAdapter(catImages)
        }

        scrollListener =
            object : EndlessRecyclerViewScrollListener(view.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    viewModel.getNextPage()
                }
            }
        view.addOnScrollListener(scrollListener)

        user.observe(viewLifecycleOwner) {
            if (it !== null) {
                viewModel = imageFeedViewModelFactory.create(it.api_key, parentFragmentManager)
                feedUpdateObserver(view)
            }
        }

        return view
    }

    private fun feedUpdateObserver(view: RecyclerView) {
        viewModel.catImages.observe(viewLifecycleOwner) {
            val newItemsCount = it.size - catImages.size
            catImages = it as ArrayList<CatImage>
            view.adapter?.notifyItemRangeInserted(catImages.size - newItemsCount - 1,newItemsCount)
        }
        viewModel.getNextPage()
    }

}