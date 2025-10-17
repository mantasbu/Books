package com.mantasbu.books.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    snackbarHostState: SnackbarHostState,
    onEvent: (HomeEvent) -> Unit,
) {
    val topAppBarScroll = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Books")
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(HomeEvent.Refresh) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                        )
                    }
                },
                scrollBehavior = topAppBarScroll,
            )
        }
    ) { inner ->
        when {
            state.isLoading && state.sections.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null && state.sections.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { onEvent(HomeEvent.Load) },
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = state.sections,
                        key = { it.listId },
                    ) { section ->
                        HomeSectionCard(
                            section = section,
                            onSeeAll = {
                                onEvent(HomeEvent.ClickSeeAll(section.listId))
                            },
                            onRetry = {
                                onEvent(HomeEvent.RetrySection(section.listId))
                            },
                            onBookClick = { id ->
                                onEvent(HomeEvent.ClickBook(id))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSectionCard(
    section: HomeSectionUi,
    onSeeAll: () -> Unit,
    onRetry: () -> Unit,
    onBookClick: (Int) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(
                onClick = onSeeAll,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RectangleShape, // square corners to match your wireframe vibe
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 6.dp,
                ),
            ) {
                Text(text = "ALL")
            }
        }

        when {
            section.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            section.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = section.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                }
            }

            section.preview.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp,
                        )
                        .height(48.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(text = "No books yet")
                }
            }

            else -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    items(
                        items = section.preview,
                        key = { it.id },
                    ) { book ->
                        BookCard(
                            book = book,
                            onClick = {
                                onBookClick(book.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookCard(
    book: BookCardUi,
    onClick: () -> Unit,
) {
    val titleStyle = MaterialTheme.typography.bodyMedium
    val maxTitleLines = 2
    val titleHeight = with(LocalDensity.current) {
        (titleStyle.lineHeight * maxTitleLines).toDp()
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier.width(148.dp),
    ) {
        Column(Modifier.fillMaxWidth()) {
            val imagePadding = 12.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = imagePadding,
                        end = imagePadding,
                        top = imagePadding,
                    )
            ) {
                AsyncImage(
                    model = book.thumbnailUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
                    contentScale = ContentScale.Fit,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp,
                    )
                    .height(titleHeight),
            ) {
                Text(
                    text = book.title,
                    style = titleStyle,
                    maxLines = maxTitleLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}