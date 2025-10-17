package com.mantasbu.books.presentation.details

data class BookDetailsUi(
    val id: Int,
    val listId: Int,
    val title: String,
    val author: String,
    val isbn: String,
    val publicationDate: String,
    val img: String,
    val description: String,
)