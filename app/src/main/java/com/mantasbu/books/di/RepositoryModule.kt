package com.mantasbu.books.di

import com.mantasbu.books.data.repository.BooksRepositoryImpl
import com.mantasbu.books.domain.repository.BooksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBooksRepository(stockRepositoryImpl: BooksRepositoryImpl): BooksRepository
}