package com.mantasbu.books.presentation.lists

sealed interface BookListEffect {
    data object NavigateBack : BookListEffect
    data class NavigateToBook(val bookId: Int) : BookListEffect
    data class ShowMessage(val message: String) : BookListEffect
}