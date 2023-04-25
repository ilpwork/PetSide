package com.example.petside

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.petside.db.MainDb
import com.example.petside.retrofit.MainApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeedFragment : Fragment() {

    private var columnCount = 1
    private var limit = 10
    private var page = 0
    private lateinit var api_key: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.feed_item_list, container, false)

        val mainDb: MainDb = MainDb.getMainDb(requireContext())
        Thread {
            api_key = mainDb.getDao().getApiKey()
        }.start()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mainApi = retrofit.create(MainApi::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {

                val catImages = mainApi.getCatImages(
                    api_key,
                    limit,
                    page
                )

                if (view is RecyclerView) {
                    with(view) {
                        layoutManager = when {
                            columnCount <= 1 -> LinearLayoutManager(context)
                            else -> GridLayoutManager(context, columnCount)
                        }
                        adapter = MyFeedRecyclerViewAdapter(catImages)
                    }
                }

            } catch (e: HttpException) {
                val dialog = AlertFragment(e.message(), ::endLoading)
                dialog.show(parentFragmentManager, "ApiKeyError")
            }
        }


        return view
    }

    fun endLoading() {

    }
}