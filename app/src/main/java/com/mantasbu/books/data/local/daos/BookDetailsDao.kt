package com.mantasbu.books.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mantasbu.books.data.local.entities.BookDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDetailsDao {
    @Query("SELECT * FROM book_details WHERE id = :id LIMIT 1")
    fun get(id: Int): Flow<BookDetailsEntity?>

    @Upsert
    suspend fun upsert(item: BookDetailsEntity)
}