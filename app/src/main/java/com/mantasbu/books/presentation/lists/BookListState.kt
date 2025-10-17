package com.mantasbu.books.presentation.lists

import com.mantasbu.books.presentation.home.BookCardUi

data class BookListState(
    val listId: Int = -1,
    val listTitle: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<BookCardUi> = emptyList(),
)