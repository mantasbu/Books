package com.mantasbu.books.presentation.home

data class BookCardUi(
    val id: Int,
    val title: String,
    val thumbnailUrl: String,
    val author: String? = null,
)