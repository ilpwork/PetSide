package com.example.petside.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.petside.R
import com.example.petside.databinding.FeedItemBinding
import com.example.petside.model.CatImage
import jp.wasabeef.glide.transformations.CropSquareTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


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

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 15f
        circularProgressDrawable.centerRadius = 60f
        circularProgressDrawable.setColorSchemeColors(Color.GREEN)
        circularProgressDrawable.start()

        Glide.with(holder.itemView.context)
            .load(currentUrl)
            .transition(withCrossFade())
            .transform(
                CropSquareTransformation(),
                RoundedCornersTransformation(16, 0, RoundedCornersTransformation.CornerType.TOP)
            )
            .placeholder(circularProgressDrawable)
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