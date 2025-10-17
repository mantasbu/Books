package com.mantasbu.books.presentation.home

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
fun HomeRoute(
    onOpenList: (Int) -> Unit,
    onOpenBook: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var lastSnackbar by remember { mutableStateOf<String?>(null) }
    var lastSnackAt by remember { mutableLongStateOf(0L) }
    val coalesceMs = 2_000L

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.ShowMessage -> {
                    val now = System.currentTimeMillis()
                    val isDup = lastSnackbar == effect.message && (now - lastSnackAt) < coalesceMs
                    if (!isDup) {
                        lastSnackbar = effect.message
                        lastSnackAt = now
                        scope.launch {
                            snackbarHost.showSnackbar(effect.message)
                        }
                    }
                }
                is HomeEffect.NavigateToList -> {
                    snackbarHost.currentSnackbarData?.dismiss()
                    onOpenList(effect.listId)
                }
                is HomeEffect.NavigateToBook -> {
                    snackbarHost.currentSnackbarData?.dismiss()
                    onOpenBook(effect.bookId)
                }
            }
        }
    }

    HomeScreen(
        state = state,
        snackbarHostState = snackbarHost,
        onEvent = viewModel::send,
    )
}