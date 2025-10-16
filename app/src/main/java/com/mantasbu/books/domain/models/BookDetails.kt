package com.mantasbu.books.domain.models

data class BookDetails(
    val id: Int,
    val listId: Int,
    val title: String,
    val img: String,
    val author: String?,
    val isbn: String?,
    val publicationDateIso: String?,
    val description: String?,
)