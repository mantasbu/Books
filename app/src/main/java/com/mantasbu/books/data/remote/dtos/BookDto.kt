package com.mantasbu.books.data.remote.dtos

import com.squareup.moshi.Json

data class BookDto(
    val id: Int,
    @field:Json(name = "list_id") val listId: Int,
    val title: String,
    val img: String,
)