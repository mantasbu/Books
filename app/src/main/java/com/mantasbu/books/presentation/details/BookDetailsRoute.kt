package com.mantasbu.books.presentation.details

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookDetailsRoute(
    bookId: Int,
    onBack: () -> Unit,
    onOpenBook: (Int) -> Unit,
    vm: BookDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(bookId) { vm.onEnter(bookId) }

    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.effects.collect { eff ->
            when (eff) {
                BookDetailsEffect.NavigateBack -> onBack()
                // is BookDetailsEffect.NavigateToList -> onOpenList(eff.listId)
                is BookDetailsEffect.NavigateToBook -> onOpenBook(eff.bookId)
                is BookDetailsEffect.ShowMessage -> snackbarHost.showSnackbar(eff.message)
            }
        }
    }

    BookDetailsScreen(
        state = state,
        snackbarHostState = snackbarHost,
        onEvent = vm::send
    )
}