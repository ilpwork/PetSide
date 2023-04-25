package com.example.petside

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petside.databinding.FeedItemBinding
import com.example.petside.retrofit.CatImage


class MyFeedRecyclerViewAdapter(
    private val values: List<CatImage>
) : RecyclerView.Adapter<MyFeedRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FeedItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val imageView: ImageView = holder.imageView
        val currentUrl: String = item.url

        Glide.with(holder.itemView.context)
            .load(currentUrl)
            .override(holder.itemView.width)
            .into(imageView)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FeedItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageView
    }

}