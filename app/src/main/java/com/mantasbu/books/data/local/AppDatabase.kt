package com.mantasbu.books.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mantasbu.books.data.local.daos.BookDao
import com.mantasbu.books.data.local.daos.BookDetailsDao
import com.mantasbu.books.data.local.daos.BookListDao
import com.mantasbu.books.data.local.entities.BookDetailsEntity
import com.mantasbu.books.data.local.entities.BookEntity
import com.mantasbu.books.data.local.entities.BookListEntity

@Database(
    entities = [
        BookListEntity::class,
        BookEntity::class,
        BookDetailsEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookListDao(): BookListDao
    abstract fun bookDao(): BookDao
    abstract fun bookDetailsDao(): BookDetailsDao
}