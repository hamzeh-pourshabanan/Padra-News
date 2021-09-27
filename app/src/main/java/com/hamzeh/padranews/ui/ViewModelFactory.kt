package com.hamzeh.padranews.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.hamzeh.padranews.data.NewsRepository
import com.hamzeh.padranews.ui.home.HomeViewModel

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: NewsRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, handle) as T
        }
        throw IllegalAccessException("Unknown ViewModel Class")
    }
}