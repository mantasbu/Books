package com.mantasbu.books.presentation.home

data class HomeSectionUi(
    val listId: Int,
    val title: String,
    val preview: List<BookCardUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)