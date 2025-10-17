package com.mantasbu.books.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_details")
data class BookDetailsEntity(
    @PrimaryKey val id: Int,
    val listId: Int,
    val isbn: String,
    val publicationDate: String,
    val author: String,
    val title: String,
    val img: String,
    val description: String,
    val updatedAt: Long,
)