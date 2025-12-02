package com.example.foxproject.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.foxproject.R
import com.example.foxproject.data.models.Fox
import com.example.foxproject.presentation.viewmodel.FoxViewModel

@Composable
fun FoxScreen(viewModel: FoxViewModel = viewModel()) {
    val foxesPagingItems = viewModel.foxesPagingFlow.collectAsLazyPagingItems()
    val errorState by viewModel.errorState.collectAsState()
    val listState = rememberLazyListState()

    // Автодогрузка при скролле
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = layoutInfo.totalItemsCount

            lastVisibleItem?.index != null &&
                    lastVisibleItem.index >= totalItems - 3 &&
                    foxesPagingItems.loadState.append !is LoadState.Loading
        }
    }

    // Обработка ошибок загрузки
    LaunchedEffect(foxesPagingItems.loadState) {
        val error = foxesPagingItems.loadState.refresh as? LoadState.Error
            ?: foxesPagingItems.loadState.append as? LoadState.Error
            ?: foxesPagingItems.loadState.prepend as? LoadState.Error

        error?.let {
            viewModel.clearError()
        }
    }
}

@Composable
fun FoxList(
    foxesPagingItems: LazyPagingItems<com.example.foxproject.data.models.Fox>,
    onFoxClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        items(
            count = foxesPagingItems.itemCount,
            key = foxesPagingItems.itemKey { it.id },
            contentType = foxesPagingItems.itemContentType { "fox" }
        ) { index ->
            val fox = foxesPagingItems[index]
            if (fox != null) {
                FoxCard(
                    fox = fox,
                    onClick = { onFoxClick(fox.index) }
                )
            }
        }

        foxesPagingItems.apply {
            when {
                // Первая загрузка
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = stringResource(R.string.loading))
                            }
                        }
                    }
                }

                // Ошибка при первой загрузке
                loadState.refresh is LoadState.Error -> {
                    item {
                        ErrorCard(
                            message = stringResource(R.string.fail_1),
                            onRetry = { retry() }
                        )
                    }
                }

                // Индикатор загрузки следующей страницы
                loadState.append is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                // Ошибка при загрузке следующей страницы
                loadState.append is LoadState.Error -> {
                    item {
                        ErrorCard(
                            message = stringResource(R.string.fail_2),
                            onRetry = { retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoxCard(
    fox: Fox,
    onClick: (Fox) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { Toast.makeText(context, context.getString(R.string.item, fox.index),
                Toast.LENGTH_SHORT).show() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = fox.imageUrl,
                contentDescription = "Fox #${fox.index}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.Button(
                onClick = onRetry
            ) {
                Text(text = stringResource(R.string.again))
            }
        }
    }
}