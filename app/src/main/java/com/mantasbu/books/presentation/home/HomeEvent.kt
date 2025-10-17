package com.mantasbu.books.presentation.home

sealed interface HomeEvent {
    data object Load : HomeEvent
    data object Refresh : HomeEvent
    data class ClickSeeAll(val listId: Int) : HomeEvent
    data class ClickBook(val bookId: Int) : HomeEvent
    data class RetrySection(val listId: Int) : HomeEvent
}