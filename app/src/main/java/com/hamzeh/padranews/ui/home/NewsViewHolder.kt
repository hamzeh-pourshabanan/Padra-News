package com.hamzeh.padranews.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hamzeh.padranews.R
import com.hamzeh.padranews.databinding.FullwithImageItemBinding
import com.hamzeh.padranews.model.News

/**
 * View Holderfor a [News]RecyclerViewlist item.
 */
class NewsViewHolder(binding: FullwithImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val image: ImageView = binding.itemImageView
    private val title: TextView = binding.itemTitle

    fun bind(news: News) {
        val circularProgressDrawable = CircularProgressDrawable(image.rootView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 35f
        circularProgressDrawable.start()
        news.urlToImage?.let {
            val uriToImage = it.toUri().buildUpon().scheme("https").build()
            Glide.with(image.context)
                .load(uriToImage)
                .apply(
                    RequestOptions()
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.ic_broken_image))
                .into(image)
        }
        title.setText(news.title)
    }



    companion object {
        fun create(parent: ViewGroup): NewsViewHolder {
            return NewsViewHolder(
                FullwithImageItemBinding
                    .inflate(LayoutInflater.from(parent.context))
            )
        }
    }

}