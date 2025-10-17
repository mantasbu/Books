package com.mantasbu.books.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mantasbu.books.data.local.entities.BookListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookListDao {
    @Query("SELECT * FROM book_lists ORDER BY title")
    fun getLists(): Flow<List<BookListEntity>>

    @Upsert
    suspend fun upsertAll(items: List<BookListEntity>)

    @Query("DELETE FROM book_lists")
    suspend fun clearAll()
}
