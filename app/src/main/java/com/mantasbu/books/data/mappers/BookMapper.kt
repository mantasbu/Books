package com.mantasbu.books.data.mappers

import com.mantasbu.books.data.remote.dtos.BookDetailsDto
import com.mantasbu.books.data.remote.dtos.BookDto
import com.mantasbu.books.data.remote.dtos.BookListDto
import com.mantasbu.books.domain.models.Book
import com.mantasbu.books.domain.models.BookDetails
import com.mantasbu.books.domain.models.BookList

fun BookDto.toDomain() = Book(
    id = id,
    listId = listId,
    title = title,
    img = img,
)

fun BookListDto.toDomain() = BookList(
    id = id,
    title = title,
)

fun BookDetailsDto.toDomain() = BookDetails(
    id = id,
    listId = listId,
    title = title,
    img = img,
    author = author,
    isbn = isbn,
    publicationDateIso = publicationDate,
    description = description,
)