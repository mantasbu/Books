package com.mantasbu.books.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [Index("listId")],
)
data class BookEntity(
    @PrimaryKey val id: Int,
    val listId: Int,
    val title: String,
    val img: String,
    val updatedAt: Long,
)