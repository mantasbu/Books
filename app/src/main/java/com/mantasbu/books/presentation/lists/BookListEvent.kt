package com.mantasbu.books.presentation.lists

sealed interface BookListEvent {
    data object Refresh : BookListEvent
    data object Back : BookListEvent
    data class ClickBook(val bookId: Int) : BookListEvent
}