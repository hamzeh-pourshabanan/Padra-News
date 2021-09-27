package com.hamzeh.padranews

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.hamzeh.padranews.api.NewsService
import com.hamzeh.padranews.data.NewsRepository
import com.hamzeh.padranews.ui.ViewModelFactory

object Injection {
    private fun provideNewsRepository(): NewsRepository {
        return NewsRepository(NewsService.create())
    }

    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideNewsRepository())
    }
}