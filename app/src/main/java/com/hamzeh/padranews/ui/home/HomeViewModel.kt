package com.hamzeh.padranews.ui.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hamzeh.padranews.api.NewsService
import com.hamzeh.padranews.data.NewsRepository
import com.hamzeh.padranews.model.News
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *  ViewModel for the [HomeFragment] screen.
 *  The ViewModel works with the [NewsRepository] to get the data.
 */
class HomeViewModel(
    private val repository: NewsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _news = MutableLiveData<List<News>>()
    var news: LiveData<List<News>> = _news

    private var articlesSize: Int = 0

    val state: StateFlow<UiState>

    val accept: (UiAction) -> Unit

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    init {
        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }
        state = searches
            .flatMapLatest { search ->
                combine(
                    queriesScrolled,
                    searchRepo(queryString = search.query),
                    ::Pair
                )
                    // Each unique PagingData should be submitted once, take the latest from
                    // queriesScrolled
                    .distinctUntilChangedBy { it.second }
                    .map { (scroll, pagingData) ->
                        UiState(
                            query = search.query,
                            pagingData = pagingData,
                            lastQueryScrolled = scroll.currentQuery,
                            // If the search query matches the scroll query, the user has scrolled
                            hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

    }

    private fun searchRepo(queryString: String): Flow<PagingData<News>> =
        repository.getSearchResultStream(queryString)
            .cachedIn(viewModelScope)
}



sealed class UiAction {
    data class Search(val query: String = "en") : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val pagingData: PagingData<News> = PagingData.empty()
)

private const val DEFAULT_CATEGORY = ""
private const val DEFAULT_LANGUAGE = "en"
private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "en"