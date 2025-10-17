package com.mantasbu.books.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    state: BookDetailsState,
    snackbarHostState: SnackbarHostState,
    onEvent: (BookDetailsEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Book")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(BookDetailsEvent.Back) },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { inner ->
        val details = state.details

        when {
            details == null && state.error != null -> {
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
                            onClick = { onEvent(BookDetailsEvent.Retry) },
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            details == null -> { // initial loading
                Box(
                    Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                // details are non-null here
                LazyColumn(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        BookHeaderSection(details)
                    }
                }
            }
        }
    }
}

@Composable
private fun BookHeaderSection(details: BookDetailsUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        ) {
        AsyncImage(
            model = details.img,
            contentDescription = details.title,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
                .width(220.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = details.title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(12.dp))

        InfoField(
            label = "Author",
            value = details.author.ifBlank { "—" },
        )
        InfoField(
            label = "ISBN",
            value = details.isbn.ifBlank { "N/A" },
        )
        InfoField(
            label = "Publication date",
            value = details.publicationDate.ifBlank { "—" },
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(6.dp))

        Text(
            text = details.description,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun InfoField(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}