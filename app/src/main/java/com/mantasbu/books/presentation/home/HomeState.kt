package com.mantasbu.books.presentation.home

data class HomeState(
    val isLoading: Boolean = false,
    val sections: List<HomeSectionUi> = emptyList(),
    val error: String? = null,
)