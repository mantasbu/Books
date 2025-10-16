package com.mantasbu.books.data.remote.dtos

import com.squareup.moshi.Json

data class BookDetailsDto(
    val id: Int,
    @field:Json(name = "list_id") val listId: Int,
    val isbn: String,
    @field:Json(name = "publication_date") val publicationDate: String,
    val author: String,
    val title: String,
    val img: String,
    val description: String,
)