package com.mantasbu.books.data.mappers

import com.mantasbu.books.data.local.entities.BookDetailsEntity
import com.mantasbu.books.data.local.entities.BookEntity
import com.mantasbu.books.data.local.entities.BookListEntity
import com.mantasbu.books.data.remote.dtos.BookDetailsDto
import com.mantasbu.books.data.remote.dtos.BookDto
import com.mantasbu.books.data.remote.dtos.BookListDto
import com.mantasbu.books.domain.models.Book
import com.mantasbu.books.domain.models.BookDetails
import com.mantasbu.books.domain.models.BookList

// DTO -> Entity
fun BookListDto.toEntity(now: Long) = BookListEntity(
    id = id,
    title = title,
    updatedAt = now,
)

fun BookDto.toEntity(now: Long) = BookEntity(
    id = id,
    listId = listId,
    title = title,
    img = img,
    updatedAt = now,
)

fun BookDetailsDto.toEntity(now: Long) = BookDetailsEntity(
    id = id,
    listId = listId,
    title = title,
    img = img,
    author = author,
    isbn = isbn,
    publicationDate = publicationDate,
    description = description,
    updatedAt = now,
)

// Entity -> Domain
fun BookListEntity.toDomain() = BookList(
    id = id,
    title = title,
)

fun BookEntity.toDomain() = Book(
    id = id,
    listId = listId,
    title = title,
    img = img,
)

fun BookDetailsEntity.toDomain() = BookDetails(
    id = id,
    listId = listId,
    title = title,
    img = img,
    author = author,
    isbn = isbn,
    publicationDateIso = publicationDate,
    description = description,
)