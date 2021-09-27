package com.hamzeh.padranews

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hamzeh.padranews.model.News
import com.hamzeh.padranews.ui.home.HomeNewsAdapter

@BindingAdapter("urlToImage")
fun bindImage(imageView: ImageView, urlToImage: String?) {
    urlToImage?.let {
        val uriToImage = it.toUri().buildUpon().scheme("https").build()
        Log.d("BindingAdapter", uriToImage.toString())
        Glide.with(imageView.context)
            .load(uriToImage)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imageView)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: PagingData<News>) {
    val adapter = recyclerView.adapter as HomeNewsAdapter
//    adapter.submitData(data)
}

