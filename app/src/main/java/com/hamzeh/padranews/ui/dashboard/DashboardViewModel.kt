package com.hamzeh.padranews.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamzeh.padranews.api.NewsService
import com.hamzeh.padranews.model.News
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class DashboardViewModel : ViewModel() {

    private val _status = MutableStateFlow("This is dashboard Fragment")
    val status: StateFlow<String> = _status

    private val _urlToImage: MutableStateFlow<String?> = MutableStateFlow("")
    val urlToImage: StateFlow<String?> = _urlToImage

    private val _news = MutableSharedFlow<News>()
    val news: SharedFlow<News> = _news



}