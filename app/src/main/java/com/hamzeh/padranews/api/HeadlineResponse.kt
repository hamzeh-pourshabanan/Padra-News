package com.hamzeh.padranews.api

import com.hamzeh.padranews.model.News

data class HeadlineResponse(
    val totalResults: Int = 0,
    val articles: List<News> = emptyList()
)