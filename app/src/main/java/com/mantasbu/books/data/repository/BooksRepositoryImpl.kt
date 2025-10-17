package com.mantasbu.books.data.repository

import com.mantasbu.books.data.local.daos.BookDao
import com.mantasbu.books.data.local.daos.BookDetailsDao
import com.mantasbu.books.data.local.daos.BookListDao
import com.mantasbu.books.data.local.entities.BookEntity
import com.mantasbu.books.data.local.entities.BookListEntity
import com.mantasbu.books.data.mappers.toDomain
import com.mantasbu.books.data.mappers.toEntity
import com.mantasbu.books.data.remote.BooksApi
import com.mantasbu.books.domain.models.Book
import com.mantasbu.books.domain.models.BookDetails
import com.mantasbu.books.domain.models.BookList
import com.mantasbu.books.domain.repository.BooksRepository
import com.mantasbu.books.domain.core.Resource
import com.mantasbu.books.domain.core.networkBoundResource
import com.mantasbu.books.domain.core.toUserMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BooksRepositoryImpl @Inject constructor(
    private val api: BooksApi,
    private val listsDao: BookListDao,
    private val booksDao: BookDao,
    private val detailsDao: BookDetailsDao,
) : BooksRepository {

    private fun now() = System.currentTimeMillis()
    private val STALE_MS = 60 * 60 * 1000L // 1 hour

    override fun getBookLists(): Flow<Resource<List<BookList>>> =
        networkBoundResource(
            query = {
                listsDao.getLists().map { it.map(BookListEntity::toDomain) }
            },
            fetch = {
                api.getBookLists()
            },
            saveFetchResult = { dtos ->
                val time = now()
                listsDao.clearAll()
                listsDao.upsertAll(dtos.map { it.toEntity(time) })
            },
            shouldFetch = { current ->
                val items = current ?: emptyList()
                items.isEmpty()
            }
        )

    override fun getBooksByList(listId: Int): Flow<Resource<List<Book>>> =
        networkBoundResource(
            query = {
                booksDao.getBooksByList(listId).map { it.map(BookEntity::toDomain) }
            },
            fetch = {
                api.getBooks()
            },
            saveFetchResult = { dtos ->
                val time = now()
                val items = dtos
                    .filter { it.listId == listId }
                    .map { it.toEntity(time) }
                booksDao.replaceBooksList(listId, items)
            },
            shouldFetch = { current ->
                val empty = current.isNullOrEmpty()
                empty || isStaleBooks(listId)
            }
        )

    override fun getBooks(listId: Int, limit: Int): Flow<Resource<List<Book>>> =
        networkBoundResource(
            query = {
                booksDao.getBooks(listId, limit).map { it.map(BookEntity::toDomain) }
            },
            fetch = {
                api.getBooks()
            },
            saveFetchResult = { dtos ->
                val time = now()
                val items = dtos
                    .filter { it.listId == listId }
                    .map { it.toEntity(time) }
                booksDao.replaceBooksList(listId, items)
            },
            shouldFetch = { current ->
                val empty = current.isNullOrEmpty()
                empty || isStaleBooks(listId)
            }
        )

    override fun getBookDetails(id: Int): Flow<Resource<BookDetails?>> = flow {
        emit(Resource.Loading())

        val dbFlow = detailsDao.get(id)

        // 1) Emit cached once, but only if present
        val cached = dbFlow.firstOrNull()
        if (cached != null) {
            emit(Resource.Success(cached.toDomain()))
        }

        // 2) Refresh if missing or stale
        val needFetch = cached == null || isStale(cached.updatedAt)
        if (needFetch) {
            try {
                val dto = api.getBookDetails(id)
                detailsDao.upsert(dto.toEntity(now()))
            } catch (t: Throwable) {
                emit(Resource.Error(t.toUserMessage()))
            }
        }

        // 3) Stream DB changes
        dbFlow
            .map { it?.toDomain() }
            .distinctUntilChanged()
            .collect { details ->
                if (details != null) {
                    emit(Resource.Success(details))
                }
            }
    }

    private suspend fun isStaleBooks(listId: Int): Boolean {
        val minTs = booksDao.minUpdatedAtForList(listId) ?: return true // empty = stale
        return now() - minTs > STALE_MS
    }

    private fun isStale(updatedAt: Long) = now() - updatedAt > STALE_MS
}