package com.mantasbu.books.domain.repository

import com.mantasbu.books.domain.models.Book
import com.mantasbu.books.domain.models.BookDetails
import com.mantasbu.books.domain.models.BookList
import com.mantasbu.books.domain.core.Resource
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    fun getBooks(listId: Int, limit: Int = 5): Flow<Resource<List<Book>>>
    fun getBookDetails(id: Int): Flow<Resource<BookDetails?>>
    fun getBookLists(): Flow<Resource<List<BookList>>>
    fun getBooksByList(listId: Int): Flow<Resource<List<Book>>>
}