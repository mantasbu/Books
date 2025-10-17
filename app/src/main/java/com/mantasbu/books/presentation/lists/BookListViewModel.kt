package com.mantasbu.books.presentation.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantasbu.books.domain.core.Resource
import com.mantasbu.books.domain.models.Book
import com.mantasbu.books.domain.repository.BooksRepository
import com.mantasbu.books.presentation.home.BookCardUi
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
class BookListViewModel @Inject constructor(
    private val repository: BooksRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(BookListState())
    val state: StateFlow<BookListState> = _state.asStateFlow()

    private val _effects = Channel<BookListEffect>(Channel.BUFFERED)
    val effects: Flow<BookListEffect> = _effects.receiveAsFlow()

    private val events = MutableSharedFlow<BookListEvent>(extraBufferCapacity = 32)

    private var currentListId: Int? = null
    private var enrichJob: Job? = null

    init {
        viewModelScope.launch {
            events.collect(::handle)
        }
    }

    fun send(event: BookListEvent) {
        events.tryEmit(event)
    }

    fun onEnter(listId: Int) {
        if (currentListId == listId) return

        currentListId = listId

        _state.update {
            it.copy(
                listId = listId,
                isLoading = true,
                error = null,
                items = emptyList(),
            )
        }

        loadListTitle(listId)
        loadBooks(listId)
    }

    private fun handle(event: BookListEvent) {
        when (event) {
            BookListEvent.Refresh -> {
                currentListId?.let { loadBooks(it) }
            }
            BookListEvent.Back -> {
                viewModelScope.launch {
                    _effects.send(BookListEffect.NavigateBack)
                }
            }
            is BookListEvent.ClickBook -> {
                viewModelScope.launch {
                    _effects.send(BookListEffect.NavigateToBook(event.bookId))
                }
            }
        }
    }

    private fun loadListTitle(listId: Int) {
        viewModelScope.launch {
            repository.getBookLists().collect { res ->
                if (res is Resource.Success) {
                    val title = res.data.orEmpty().firstOrNull { it.id == listId }?.title.orEmpty()
                    _state.update { it.copy(listTitle = title.ifBlank { "Books" }) }
                }
            }
        }
    }

    private fun loadBooks(listId: Int) {
        // cancel any previous enrichment
        enrichJob?.cancel()

        viewModelScope.launch {
            repository.getBooksByList(listId).collect { res ->
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
                        val msg = res.message ?: "Failed to load books"
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = msg,
                                items = emptyList(),
                            )
                        }
                        _effects.send(BookListEffect.ShowMessage(msg))
                    }
                    is Resource.Success -> {
                        val rows = res.data.orEmpty().map { it.toCardUi() }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                items = rows,
                            )
                        }
                        // start background author enrichment
                        enrichAuthors(rows.map { it.id })
                    }
                }
            }
        }
    }

    private fun enrichAuthors(ids: List<Int>) {
        enrichJob = viewModelScope.launch {
            ids.forEach { id ->
                launch {
                    repository.getBookDetails(id).collect { r ->
                        if (r is Resource.Success) {
                            val details = r.data
                            val author = details?.author?.takeIf { it.isNotBlank() } ?: return@collect
                            _state.update { s ->
                                val list = s.items.toMutableList()
                                val idx = list.indexOfFirst { it.id == id }
                                if (idx != -1 && list[idx].author != author) {
                                    list[idx] = list[idx].copy(author = author)
                                }
                                s.copy(items = list)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Book.toCardUi() = BookCardUi(
    id = id,
    title = title,
    thumbnailUrl = img,
    author = null,
)