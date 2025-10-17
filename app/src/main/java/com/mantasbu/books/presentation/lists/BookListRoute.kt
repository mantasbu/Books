package com.mantasbu.books.presentation.lists

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    // Guards to prevent double navigation
    var navigatingToBook by remember { mutableStateOf(false) }
    var poppingBack by remember { mutableStateOf(false) }

    // Coalesce duplicate toasts like "No internet connection"
    var lastSnackbar by remember { mutableStateOf<String?>(null) }
    var lastSnackAt by remember { mutableLongStateOf(0L) }
    val coalesceMs = 2_000L

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is BookListEffect.ShowMessage -> {
                    val now = System.currentTimeMillis()
                    val dup = lastSnackbar == effect.message && (now - lastSnackAt) < coalesceMs
                    if (!dup) {
                        lastSnackbar = effect.message
                        lastSnackAt = now
                        scope.launch {
                            snackbarHost.showSnackbar(effect.message)
                        }
                    }
                }
                is BookListEffect.NavigateToBook -> {
                    if (!navigatingToBook) {
                        navigatingToBook = true
                        snackbarHost.currentSnackbarData?.dismiss()
                        onOpenBook(effect.bookId)
                    }
                }
                BookListEffect.NavigateBack -> {
                    if (!poppingBack) {
                        poppingBack = true
                        snackbarHost.currentSnackbarData?.dismiss()
                        onBack()
                    }
                }
            }
        }
    }

    // Hardware back: dismiss snackbar first, then navigate back
    BackHandler {
        snackbarHost.currentSnackbarData?.dismiss()
        viewModel.send(BookListEvent.Back)
    }

    BookListScreen(
        state = state,
        snackbarHostState = snackbarHost,
        onEvent = viewModel::send,
    )
}