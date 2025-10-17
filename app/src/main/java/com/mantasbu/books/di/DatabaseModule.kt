package com.mantasbu.books.di

import android.app.Application
import androidx.room.Room
import com.mantasbu.books.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDb(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "app.db").build()

    @Provides
    fun provideBookListDao(db: AppDatabase) = db.bookListDao()

    @Provides
    fun provideBookDao(db: AppDatabase) = db.bookDao()

    @Provides
    fun provideBookDetailsDao(db: AppDatabase) = db.bookDetailsDao()
}