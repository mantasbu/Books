package com.mantasbu.books.presentation.lists

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookListRoute(
    listId: Int,
    onBack: () -> Unit,
    onOpenBook: (Int) -> Unit,
    viewModel: BookListViewModel = hiltViewModel(),
) {
    LaunchedEffect(listId) {
        viewModel.onEnter(listId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BookListEffect.NavigateBack -> onBack()
                is BookListEffect.NavigateToBook -> onOpenBook(effect.bookId)
                is BookListEffect.ShowMessage -> snackbarHost.showSnackbar(effect.message)
            }
        }
    }

    BookListScreen(
        state = state,
        snackbarHostState = snackbarHost,
        onEvent = viewModel::send
    )
}