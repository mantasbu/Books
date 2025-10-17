package com.mantasbu.books.presentation.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    onOpenList: (Int) -> Unit,
    onOpenBook: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    // Handle one-off effects (navigation, snackbars)
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToList -> onOpenList(effect.listId)
                is HomeEffect.NavigateToBook -> onOpenBook(effect.bookId)
                is HomeEffect.ShowMessage -> snackbarHost.showSnackbar(effect.message)
            }
        }
    }

    HomeScreen(
        state = state,
        snackbarHostState = snackbarHost,
        onEvent = viewModel::send,
    )
}