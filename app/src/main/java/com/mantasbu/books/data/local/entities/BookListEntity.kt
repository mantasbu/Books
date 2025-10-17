package com.mantasbu.books.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_lists")
data class BookListEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val updatedAt: Long,
)