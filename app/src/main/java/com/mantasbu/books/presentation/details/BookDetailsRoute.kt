package com.mantasbu.books.presentation.details

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun BookDetailsRoute(
    bookId: Int,
    onBack: () -> Unit,
    onOpenBook: (Int) -> Unit,
    viewModel: BookDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(bookId) {
        viewModel.onEnter(bookId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // prevent double pop if user taps back repeatedly
    var navigatingBack by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is BookDetailsEffect.ShowMessage -> {
                    scope.launch {
                        snackbarHost.showSnackbar(effect.message)
                    }
                }

                BookDetailsEffect.NavigateBack -> {
                    // dismiss any visible snackbar and guard against a second pop
                    snackbarHost.currentSnackbarData?.dismiss()
                    if (!navigatingBack) {
                        navigatingBack = true
                        onBack()
                    }
                }

                is BookDetailsEffect.NavigateToBook -> {
                    onOpenBook(effect.bookId)
                }
            }
        }
    }

    BookDetailsScreen(
        state = state,
        snackbarHostState = snackbarHost,
        onEvent = viewModel::send,
    )

    BackHandler {
        // dismiss snackbar so back is immediate
        snackbarHost.currentSnackbarData?.dismiss()
        viewModel.send(BookDetailsEvent.Back)
    }
}