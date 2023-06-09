package com.example.petside.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.petside.R
import com.example.petside.databinding.FeedItemBinding
import com.example.domain.model.CatImage
import com.example.petside.viewmodel.ImageFeedViewModel
import jp.wasabeef.glide.transformations.CropSquareTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import retrofit2.HttpException


class MyFeedRecyclerViewAdapter :
    PagingDataAdapter<CatImage, MyFeedRecyclerViewAdapter.ViewHolder>(CAT_IMAGE_DIFF_CALLBACK) {
    private lateinit var viewModel: ImageFeedViewModel
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
        val item = getItem(position)
        if (item !== null) {
            val imageView: ImageView = holder.imageView
            val addToFavouritesButton: ImageButton = holder.addToFavouritesButton
            val upVoteButton: ImageButton = holder.upVoteButton
            val downVoteButton: ImageButton = holder.downVoteButton
            val moreButton: ImageButton = holder.moreButton

            val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
            circularProgressDrawable.strokeWidth = 15f
            circularProgressDrawable.centerRadius = 60f
            circularProgressDrawable.setColorSchemeColors(Color.GREEN)
            circularProgressDrawable.start()

            Glide.with(holder.itemView.context)
                .load(item.url)
                .transition(withCrossFade())
                .transform(
                    CropSquareTransformation(),
                    RoundedCornersTransformation(16, 0, RoundedCornersTransformation.CornerType.TOP)
                )
                .placeholder(circularProgressDrawable)
                .into(imageView)

            if (item.favourite !== null) {
                addToFavouritesButton.setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue2
                    )
                )
            } else {
                addToFavouritesButton.setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.gray3
                    )
                )
            }

            addToFavouritesButton.setOnClickListener {
                (it as ImageButton).isEnabled = false

                /*
                fun onError(e: HttpException) {
                    it.isEnabled = true
                }
    */

                if (item.favourite !== null) {
                    fun onSuccess() {
                        it.isEnabled = true
                        addToFavouritesButton.setColorFilter(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.gray3
                            )
                        )
                    }
                    viewModel.deleteFromFavourites(position, ::onSuccess)
                } else {
                    fun onSuccess() {
                        it.isEnabled = true
                        addToFavouritesButton.setColorFilter(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.blue2
                            )
                        )
                    }
                    viewModel.addToFavourites(position, ::onSuccess)
                }
            }

            upVoteButton.setOnClickListener {
                (it as ImageButton).setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue2
                    )
                )
            }

            downVoteButton.setOnClickListener {
                (it as ImageButton).setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue2
                    )
                )
            }

            moreButton.setOnClickListener {
                (it as ImageButton).setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue2
                    )
                )
            }
        }


    }

    fun addViewModel(imageFeedViewModel: ImageFeedViewModel) {
        viewModel = imageFeedViewModel
    }

    inner class ViewHolder(binding: FeedItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageView
        val addToFavouritesButton = binding.addToFavouritesButton
        val upVoteButton = binding.upVoteButton
        val downVoteButton = binding.downVoteButton
        val moreButton = binding.moreButton
    }

    companion object {
        private val CAT_IMAGE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<CatImage>() {
            override fun areItemsTheSame(oldItem: CatImage, newItem: CatImage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CatImage, newItem: CatImage): Boolean {
                return oldItem.url == newItem.url
            }
        }
    }

}