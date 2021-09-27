package com.hamzeh.padranews.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.hamzeh.padranews.databinding.FullwithImageItemBinding
import com.hamzeh.padranews.model.News

class HomeNewsAdapter: PagingDataAdapter<News, NewsViewHolder>(NEWS_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = getItem(position)
        Log.d("homenewsadapter", "$newsItem")

            if (newsItem != null) {
                holder.bind(newsItem)
                Log.d("homenewsadapter", newsItem.title)
            }

    }

    companion object {
        private val NEWS_COMPARATOR = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
                oldItem.title == newItem.title
        }
    }
}