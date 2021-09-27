package com.hamzeh.padranews.model

import androidx.room.PrimaryKey

data class News(
    val title: String = "",
    val description: String? = "",
    val urlToImage: String? = "",
    val url: String = "",
    val author: String? = "",
    val publishedAt: String? = "",
    val content: String? = "",
    val source: Source = Source("", "")
)

data class Source(val id: String? = "", val name: String = "")
