package com.mantasbu.books.data.remote

import com.mantasbu.books.data.remote.dtos.BookDetailsDto
import com.mantasbu.books.data.remote.dtos.BookDto
import com.mantasbu.books.data.remote.dtos.BookListDto
import retrofit2.http.GET
import retrofit2.http.Path

interface BooksApi {
    @GET("books")
    suspend fun getBooks(): List<BookDto>

    @GET("books/{id}")
    suspend fun getBookDetails(
        @Path("id") id: Int,
    ): BookDetailsDto

    @GET("lists")
    suspend fun getBookLists(): List<BookListDto>
}