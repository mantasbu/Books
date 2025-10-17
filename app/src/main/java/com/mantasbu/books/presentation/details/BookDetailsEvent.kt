package com.mantasbu.books.presentation.details

sealed interface BookDetailsEvent {
    data object Retry : BookDetailsEvent
    data object Back : BookDetailsEvent
}