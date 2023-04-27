package com.example.petside.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.example.petside.R
import com.example.petside.app.App
import com.example.petside.db.UserEntity
import com.example.petside.model.CatImage
import com.example.petside.viewmodel.ImageFeedViewModel
import javax.inject.Inject

class FeedFragment : Fragment() {

    @Inject lateinit var user: LiveData<UserEntity>
    @Inject lateinit var imageFeedViewModelFactory : ImageFeedViewModel.Factory

    private var columnCount = 1
    private var catImages = ArrayList<CatImage>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App)
            .appComponent
            .inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.feed_item_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyFeedRecyclerViewAdapter(catImages)
            }
        }

        user.observe(viewLifecycleOwner) {
            if (it !== null) {
                feedUpdateObserver(it.api_key)
            }
        }

        return view
    }

    private fun feedUpdateObserver(apiKey: String) {
        val viewModel = imageFeedViewModelFactory.create(apiKey)
        viewModel.catImages.observe(viewLifecycleOwner) {
            catImages = it as ArrayList<CatImage>
        }
        viewModel.getNextPage()
    }

}