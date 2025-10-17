package com.mantasbu.books.data.repository

import com.mantasbu.books.data.mappers.toDomain
import com.mantasbu.books.data.remote.BooksApi
import com.mantasbu.books.domain.models.Book
import com.mantasbu.books.domain.models.BookDetails
import com.mantasbu.books.domain.models.BookList
import com.mantasbu.books.domain.repository.BooksRepository
import com.mantasbu.books.domain.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class BooksRepositoryImpl @Inject constructor(
    private val api: BooksApi,
) : BooksRepository {
    override fun getBookLists(): Flow<Resource<List<BookList>>> = flow {
        emit(Resource.Loading())
        try {
            val lists = api.getBookLists().map { it.toDomain() }
            emit(Resource.Success(lists))
        } catch (t: Throwable) {
            emit(Resource.Error(message = t.toUserMessage()))
        }
    }

    override fun getBooksByList(listId: Int): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading())
        try {
            val books = api.getBooks()
                .filter { it.listId == listId }
                .map { it.toDomain() }
            emit(Resource.Success(books))
        } catch (t: Throwable) {
            emit(Resource.Error(message = t.toUserMessage()))
        }
    }

    override fun getBooks(listId: Int, limit: Int): Flow<Resource<List<Book>>> = flow {
        emit(Resource.Loading())
        try {
            val preview = api.getBooks()
                .asSequence()
                .filter { it.listId == listId }
                .take(limit)
                .map { it.toDomain() }
                .toList()
            emit(Resource.Success(preview))
        } catch (t: Throwable) {
            emit(Resource.Error(message = t.toUserMessage()))
        }
    }

    override fun getBookDetails(id: Int): Flow<Resource<BookDetails?>> = flow {
        emit(Resource.Loading())
        try {
            val dto = api.getBookDetails(id)
            emit(Resource.Success(dto.toDomain()))
        } catch (t: Throwable) {
            val is404 = (t as? HttpException)?.code() == 404
            if (is404) emit(Resource.Success(null))
            else emit(Resource.Error(message = t.toUserMessage()))
        }
    }
}

/** Map exceptions to a readable UI message */
private fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> "Network error ${code()}"
    is java.net.UnknownHostException -> "No internet connection"
    is java.net.SocketTimeoutException -> "Request timed out"
    else -> message ?: "Unexpected error"
}