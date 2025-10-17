package com.mantasbu.books.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantasbu.books.domain.core.Resource
import com.mantasbu.books.domain.models.Book
import com.mantasbu.books.domain.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BooksRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = Channel<HomeEffect>(Channel.BUFFERED)
    val effects: Flow<HomeEffect> = _effects.receiveAsFlow()

    private val events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 32)

    // keep track of running preview jobs per list to cancel on refresh
    private val previewJobs = mutableMapOf<Int, Job>()

    init {
        // wire events -> reducers
        viewModelScope.launch {
            events.collect { event -> handle(event) }
        }
        // first load
        send(HomeEvent.Load)
    }

    fun send(event: HomeEvent) {
        events.tryEmit(event)
    }

    private fun handle(event: HomeEvent) {
        when (event) {
            HomeEvent.Load,
            HomeEvent.Refresh -> loadListsAndPreviews()

            is HomeEvent.RetrySection -> startPreviewFor(listId = event.listId)

            is HomeEvent.ClickSeeAll -> {
                viewModelScope.launch {
                    _effects.send(HomeEffect.NavigateToList(event.listId))
                }
            }

            is HomeEvent.ClickBook -> {
                viewModelScope.launch {
                    _effects.send(HomeEffect.NavigateToBook(event.bookId))
                }
            }
        }
    }

    private fun loadListsAndPreviews() {
        // cancel all running preview jobs when reloading
        previewJobs.values.forEach { it.cancel() }
        previewJobs.clear()

        viewModelScope.launch {
            repository.getBookLists()
                .collect { res ->
                    when (res) {
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = true,
                                    error = null,
                                )
                            }
                        }

                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = res.message, sections = emptyList(),
                                )
                            }
                            _effects.send(HomeEffect.ShowMessage(res.message ?: "Failed to load lists"))
                        }

                        is Resource.Success -> {
                            val lists = res.data.orEmpty()
                            // show sections immediately, mark each as loading (previews will fill in)
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = null,
                                    sections = lists.map { l ->
                                        HomeSectionUi(
                                            listId = l.id,
                                            title = l.title,
                                            preview = emptyList(),
                                            isLoading = true,
                                            error = null
                                        )
                                    }
                                )
                            }

                            // kick off preview loads in parallel
                            lists.forEach {
                                startPreviewFor(it.id)
                            }
                        }
                    }
                }
        }
    }

    private fun startPreviewFor(listId: Int) {
        // cancel previous job for this section if any
        previewJobs[listId]?.cancel()

        val job = viewModelScope.launch {
            repository.getBooks(listId = listId, limit = 5)
                .collect { res ->
                    when (res) {
                        is Resource.Loading -> {
                            updateSection(listId) { it.copy(isLoading = true, error = null) }
                        }

                        is Resource.Error -> {
                            updateSection(listId) { it.copy(isLoading = false, error = res.message) }
                            _effects.send(HomeEffect.ShowMessage(res.message ?: "Failed to load books"))
                        }

                        is Resource.Success -> {
                            val cards = res.data.orEmpty().map { it.toCardUi() }
                            updateSection(listId) { it.copy(isLoading = false, error = null, preview = cards) }
                        }
                    }
                }
        }
        previewJobs[listId] = job
    }

    private fun updateSection(listId: Int, transform: (HomeSectionUi) -> HomeSectionUi) {
        _state.update { s ->
            val idx = s.sections.indexOfFirst { it.listId == listId }
            if (idx == -1) {
                s
            } else {
                val mutable = s.sections.toMutableList()
                mutable[idx] = transform(mutable[idx])
                s.copy(sections = mutable)
            }
        }
    }
}

private fun Book.toCardUi() = BookCardUi(
    id = id,
    title = title,
    thumbnailUrl = img,
)