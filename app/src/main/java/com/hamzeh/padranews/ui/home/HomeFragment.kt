package com.hamzeh.padranews.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.hamzeh.padranews.Injection
import com.hamzeh.padranews.R
import com.hamzeh.padranews.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this))
            .get(HomeViewModel::class.java)

        binding.viewmodel = viewModel

        binding.bindState(
            uiState = viewModel.state,
            uiActions = viewModel.accept
        )

        coordinateMotion(binding.root)
        return binding.root
    }

    private fun coordinateMotion(view: View) {
        // TODO: set progress of MotionLayout based on an AppBarLayout.OnOffsetChangedListener
        val appBarLayout: AppBarLayout = view.findViewById(R.id.appbar_layout)
        val motionLayout: MotionLayout = view.findViewById(R.id.motion_layout)

        val listener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }

        appBarLayout.addOnOffsetChangedListener(listener)

    }

    /**
     * Binds the [UiState] provided  by the [HomeViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun FragmentHomeBinding.bindState(
        uiState: StateFlow<UiState>,
        uiActions: (UiAction) -> Unit
    ) {
        val newsAdapter = HomeNewsAdapter()
        newsList.adapter = newsAdapter
        Log.d("homeFragment", "adapter: ${newsAdapter.itemCount}")
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        newsList.addItemDecoration(decoration)
//        bindSearch(
//            uiState = uiState,
//            onQueryChanged = uiActions
//        )
        bindList(
            newsAdapter = newsAdapter,
            uiState = uiState,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentHomeBinding.bindList(
        newsAdapter: HomeNewsAdapter,
        uiState: StateFlow<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        newsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(uiState.value.query))
            }
        })

        val notLoading = newsAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.refresh is LoadState.NotLoading }
            .distinctUntilChanged()

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        val pagingData = uiState
            .map { it.pagingData }
            .distinctUntilChanged()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(shouldScrollToTop, pagingData, ::Pair)
                    // Each unique PagingData should be submitted once, take the latest from
                    // shouldScrollToTop
                    .distinctUntilChangedBy { it.second }
                    .collectLatest { (shouldScroll, pagingData) ->
                        Log.d("homeFragment", "${pagingData}")
                        newsAdapter.submitData(pagingData)
                        // Scroll only after the data has been submitted to the adapter,
                        // and is a fresh search
                        if (shouldScroll) newsList.scrollToPosition(0)
                    }
            }

        }
    }
}