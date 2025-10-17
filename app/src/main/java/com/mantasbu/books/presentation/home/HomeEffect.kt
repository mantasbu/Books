package com.mantasbu.books.presentation.home

sealed interface HomeEffect {
    data class NavigateToList(val listId: Int) : HomeEffect
    data class NavigateToBook(val bookId: Int) : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
}