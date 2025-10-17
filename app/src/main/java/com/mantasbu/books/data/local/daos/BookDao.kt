package com.mantasbu.books.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mantasbu.books.data.local.entities.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE listId = :listId ORDER BY title")
    fun getBooksByList(listId: Int): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE listId = :listId ORDER BY title LIMIT :limit")
    fun getBooks(listId: Int, limit: Int): Flow<List<BookEntity>>

    @Upsert
    suspend fun upsertAll(items: List<BookEntity>)

    @Query("DELETE FROM books WHERE listId = :listId")
    suspend fun deleteByList(listId: Int)

    @Transaction
    suspend fun replaceBooksList(listId: Int, items: List<BookEntity>) {
        deleteByList(listId)
        upsertAll(items)
    }

    @Query("SELECT MIN(updatedAt) FROM books WHERE listId = :listId")
    suspend fun minUpdatedAtForList(listId: Int): Long?
}