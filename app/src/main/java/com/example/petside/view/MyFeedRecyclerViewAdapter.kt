package com.example.petside.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petside.databinding.FeedItemBinding
import com.example.petside.model.CatImage


class MyFeedRecyclerViewAdapter : RecyclerView.Adapter<MyFeedRecyclerViewAdapter.ViewHolder>() {

    private var catImages = ArrayList<CatImage>()
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
        val item = catImages[position]
        val imageView: ImageView = holder.imageView
        val currentUrl: String = item.url

        Glide.with(holder.itemView.context)
            .load(currentUrl)
            .override(holder.itemView.width, 600)
            .into(imageView)
    }

    fun addCatImages(newImages: ArrayList<CatImage>) {
        catImages = newImages
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = catImages.size

    inner class ViewHolder(binding: FeedItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageView
    }

}