package com.mantasbu.books.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantasbu.books.domain.core.Resource
import com.mantasbu.books.domain.models.BookDetails
import com.mantasbu.books.domain.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
class BookDetailsViewModel @Inject constructor(
    private val repository: BooksRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailsState())
    val state: StateFlow<BookDetailsState> = _state.asStateFlow()

    private val _effects = Channel<BookDetailsEffect>(Channel.BUFFERED)
    val effects: Flow<BookDetailsEffect> = _effects.receiveAsFlow()

    private val events = MutableSharedFlow<BookDetailsEvent>(extraBufferCapacity = 32)

    private var currentBookId: Int? = null

    init {
        viewModelScope.launch {
            events.collect(::handle)
        }
    }

    fun send(event: BookDetailsEvent) {
        events.tryEmit(event)
    }

    fun onEnter(bookId: Int) {
        if (currentBookId == bookId) return

        currentBookId = bookId

        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                details = null,
            )
        }

        loadDetailsAndRelated(bookId)
    }

    private fun handle(event: BookDetailsEvent) {
        when (event) {
            BookDetailsEvent.Retry -> {
                currentBookId?.let {
                    loadDetailsAndRelated(it)
                }
            }
            BookDetailsEvent.Back -> {
                viewModelScope.launch {
                    _effects.send(BookDetailsEffect.NavigateBack)
                }
            }
        }
    }

    private fun loadDetailsAndRelated(bookId: Int) = viewModelScope.launch {
        repository.getBookDetails(bookId)
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
                        val msg = res.message ?: "Failed to load a book"
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = msg,
                                details = null,
                            )
                        }
                        _effects.send(BookDetailsEffect.ShowMessage(msg))
                    }
                    is Resource.Success -> {
                        val details = res.data
                        if (details == null) {
                            _state.update {
                                it.copy(isLoading = false, error = "Book was not found")
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = null,
                                    details = details.toUi()
                                )
                            }
                        }
                    }
                }
            }
        }
}

private fun BookDetails.toUi() = BookDetailsUi(
    id = id,
    listId = listId,
    title = title,
    author = author ?: "Unknown",
    isbn = isbn ?: "Unknown",
    publicationDate = publicationDateIso ?: "Unknown",
    img = img,
    description = description ?: "Unknown",
)