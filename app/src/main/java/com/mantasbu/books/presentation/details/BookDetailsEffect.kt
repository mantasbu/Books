package com.mantasbu.books.presentation.details

interface BookDetailsEffect {
    data object NavigateBack : BookDetailsEffect
    data class NavigateToBook(val bookId: Int) : BookDetailsEffect
    data class ShowMessage(val message: String) : BookDetailsEffect
}