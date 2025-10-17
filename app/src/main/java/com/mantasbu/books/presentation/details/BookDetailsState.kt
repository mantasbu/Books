package com.mantasbu.books.presentation.details

data class BookDetailsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val details: BookDetailsUi? = null,
)