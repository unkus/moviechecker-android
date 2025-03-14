/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.moviechecker.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import ru.moviechecker.CheckerTopAppBar
import ru.moviechecker.R
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodeView
import ru.moviechecker.database.episodes.IEpisodeView
import ru.moviechecker.ui.AppViewModelProvider
import ru.moviechecker.ui.navigation.NavigationDestination
import ru.moviechecker.ui.theme.MoviecheckerTheme
import ru.moviechecker.workers.AsyncCleanupDataWorker
import ru.moviechecker.workers.RetrieveDataWorker
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToEpisodeUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    val shouldShowOnlyFavorites = remember {
        mutableStateOf(false)
    }
    val shouldShowViewedEpisodes = remember {
        mutableStateOf(true)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CheckerTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        shouldShowOnlyFavorites.value = !shouldShowOnlyFavorites.value
                    }) {
                        Icon(
                            imageVector = if (shouldShowOnlyFavorites.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (shouldShowOnlyFavorites.value) Color.Yellow else Color.Gray
                        )
                    }
                    IconButton(onClick = {
                        shouldShowViewedEpisodes.value = !shouldShowViewedEpisodes.value
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (shouldShowViewedEpisodes.value) Color.Green else Color.Gray
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    WorkManager.getInstance(context)
                        .beginUniqueWork(
                            AsyncCleanupDataWorker::class.java.simpleName,
                            ExistingWorkPolicy.KEEP,
                            OneTimeWorkRequest.from(AsyncCleanupDataWorker::class.java)
                        )
                        .enqueue()
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cleanup)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            episodeList = homeUiState.episodeList,
            onEpisodeClick = { viewModel.markEpisodeViewed(it) },
            onEpisodeLongClick = {}, //navigateToEpisodeUpdate,
            onFavoriteIconClick = { viewModel.switchFavoritesMark(it) },
            onViewedIconClick = { viewModel.switchViewedMark(it) },
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            showFavorites = shouldShowOnlyFavorites.value,
            showViewedEpisodes = shouldShowViewedEpisodes.value
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeBody(
    episodeList: List<IEpisodeView>,
    onEpisodeClick: (Int) -> Unit,
    onEpisodeLongClick: (Int) -> Unit,
    onFavoriteIconClick: (Int) -> Unit,
    onViewedIconClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showFavorites: Boolean,
    showViewedEpisodes: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            val workManager = WorkManager.getInstance(context);
            val workRequest = OneTimeWorkRequestBuilder<RetrieveDataWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresStorageNotLow(true)
                        .build()
                )
                .build()

            workManager.beginUniqueWork(
                    RetrieveDataWorker::class.java.simpleName,
                    ExistingWorkPolicy.KEEP,
                    workRequest
                )
                .enqueue()

            workManager.getWorkInfoByIdFlow(workRequest.id)
                .collect { workInfo ->
                    workInfo?.let {
                        Log.d(this.javaClass.simpleName, "Статус обновления: ${workInfo.state}")
                        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            isRefreshing = false
                        }
                    }
                }
            isRefreshing = false
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        EpisodeList(
            episodeList = episodeList,
            onEpisodeClick = { onEpisodeClick(it.episodeId) },
            onEpisodeLongClick = { onEpisodeLongClick(it.episodeId) },
            onFavoriteIconClick = { onFavoriteIconClick(it.movieId) },
            onViewedIconClick = { onViewedIconClick(it.episodeId) },
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
            showNonFavorites = showFavorites,
            showViewed = showViewedEpisodes
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EpisodeList(
    episodeList: List<IEpisodeView>,
    onEpisodeClick: (IEpisodeView) -> Unit,
    onEpisodeLongClick: (IEpisodeView) -> Unit,
    onFavoriteIconClick: (IEpisodeView) -> Unit,
    onViewedIconClick: (IEpisodeView) -> Unit,
    modifier: Modifier = Modifier,
    showNonFavorites: Boolean,
    showViewed: Boolean
) {
    val context = LocalContext.current
    LazyColumn(modifier = modifier) {
        items(items = episodeList, key = { it.episodeId }) { episodeView ->
            AnimatedVisibility(
                visible = (showNonFavorites || episodeView.movieFavoritesMark)
                        && (showViewed || episodeView.episodeState != EpisodeState.VIEWED),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EpisodeItem(
                    episode = episodeView,
                    onFavoriteIconClick = { onFavoriteIconClick(it) },
                    onViewedIconClick = { onViewedIconClick(it) },
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .combinedClickable(
                            onClick = {
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(episodeView.episodeLink)
                                )
                                context.startActivity(browserIntent)

                                onEpisodeClick(episodeView)
                            },
                            onLongClick = { onEpisodeLongClick(episodeView) })
                )
            }
        }
    }
}

@Composable
private fun EpisodeItem(
    episode: IEpisodeView,
    onFavoriteIconClick: (IEpisodeView) -> Unit,
    onViewedIconClick: (IEpisodeView) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                Column(modifier = Modifier.wrapContentWidth()) {
                    Row {
                        val imageData = episode.seasonPoster ?: episode.moviePoster
                        var image: ImageBitmap? = null
                        imageData?.let {
                            try {
                                image = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                                    .asImageBitmap()
                            } catch (e: Exception) {
                                Log.e(
                                    this.javaClass.simpleName,
                                    stringResource(id = R.string.poster_error)
                                )
                            }
                        }
                        image?.let {
                            Image(
                                bitmap = it,
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(Icons.Default.Favorite.defaultWidth * 2)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = if (episode.movieFavoritesMark) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.clickable { onFavoriteIconClick(episode) },
                            tint = if (episode.movieFavoritesMark) Color.Yellow else Color.Gray
                        )
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.clickable { onViewedIconClick(episode) },
                            tint = if (episode.episodeState == EpisodeState.VIEWED) Color.Green else Color.Gray
                        )
                    }
                }
                Column {
                    if (episode.seasonTitle == null
                        || !episode.seasonTitle!!.startsWith(episode.movieTitle)
                    ) {
                        Text(
                            text = episode.movieTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = episode.seasonTitle ?: stringResource(
                            R.string.season_serial_number, episode.seasonNumber
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val episodeColor = if (episode.episodeNumber == 1) {
                            Color.Green
                        } else {
                            Color.Gray
                        }
                        Text(
                            text = episode.episodeTitle?.let {
                                stringResource(
                                    R.string.episode_serial_number_titled, it, episode.episodeNumber
                                )
                            } ?: episode.episodeTitle ?: stringResource(
                                R.string.episode_serial_number, episode.episodeNumber
                            ),
                            color = episodeColor,
                            style = MaterialTheme.typography.titleSmall
                        )
//                        Spacer(Modifier.weight(1f))
                        val date = episode.episodeDate
                        val dateString: String
                        if (date.isEqual(LocalDate.now().atStartOfDay())) {
                            dateString = stringResource(R.string.today_unstable)
                        } else if (date.isAfter(LocalDate.now().plusDays(2).atStartOfDay())) {
                            dateString =
                                date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                        } else if (date.isAfter(LocalDate.now().plusDays(1).atStartOfDay())) {
                            dateString = stringResource(
                                R.string.tomorrow_time,
                                date.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                            )
                        } else if (date.isAfter(LocalDate.now().atStartOfDay())) {
                            dateString = stringResource(
                                R.string.today_time, date.format(
                                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                                )
                            )
                        } else if (date.isAfter(LocalDate.now().minusDays(1).atStartOfDay())) {
                            dateString = stringResource(
                                R.string.yesterday_time,
                                date.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                            )
                        } else {
                            dateString =
                                date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                        }
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    MoviecheckerTheme {
        HomeBody(
            listOf(
                EpisodeView(
                    siteId = 1,
                    siteAddress = URI.create("stub"),
                    movieId = 1,
                    moviePageId = "first_movie",
                    movieTitle = "First Movie",
                    movieFavoritesMark = true,
                    seasonId = 1,
                    seasonNumber = 1,
                    seasonTitle = null,
                    episodeId = 1,
                    episodeNumber = 1,
                    episodeTitle = "Some episode of first movie with long title",
                    episodeLink = "stub",
                    episodeState = EpisodeState.VIEWED,
                    episodeDate = LocalDateTime.now().minusDays(2)
                ), EpisodeView(
                    siteId = 1,
                    siteAddress = URI.create("stub"),
                    movieId = 2,
                    moviePageId = "second_movie",
                    movieTitle = "Second Movie",
                    seasonId = 2,
                    seasonNumber = 1,
                    seasonTitle = null,
                    episodeId = 2,
                    episodeNumber = 1,
                    episodeTitle = null,
                    episodeLink = "stub",
                    episodeState = EpisodeState.RELEASED,
                    episodeDate = LocalDateTime.now().minusDays(1)
                ), EpisodeView(
                    siteId = 1,
                    siteAddress = URI.create("stub"),
                    movieId = 2,
                    moviePageId = "second_movie",
                    movieTitle = "Second Movie",
                    seasonId = 3,
                    seasonNumber = 2,
                    seasonTitle = "Season of second movie",
                    episodeId = 3,
                    episodeNumber = 1,
                    episodeTitle = null,
                    episodeLink = "stub",
                    episodeState = EpisodeState.EXPECTED,
                    episodeDate = LocalDateTime.now()
                ), EpisodeView(
                    siteId = 1,
                    siteAddress = URI.create("stub"),
                    movieId = 1,
                    moviePageId = "first_movie",
                    movieTitle = "First Movie",
                    seasonId = 1,
                    seasonNumber = 1,
                    seasonTitle = null,
                    episodeId = 4,
                    episodeNumber = 2,
                    episodeTitle = "Some episode of first movie",
                    episodeLink = "stub",
                    episodeState = EpisodeState.EXPECTED,
                    episodeDate = LocalDateTime.now().plusDays(1)
                )
            ),
            onEpisodeClick = {},
            onEpisodeLongClick = {},
            onFavoriteIconClick = {},
            onViewedIconClick = {},
            showFavorites = true,
            showViewedEpisodes = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CheckerEpisodePreview() {
    MoviecheckerTheme {
        EpisodeItem(
            EpisodeView(
                siteId = 1,
                siteAddress = URI.create("stub"),
                movieId = 1,
                moviePageId = "first_movie",
                movieTitle = "First Movie",
                seasonId = 1,
                seasonNumber = 1,
                seasonTitle = null,
                seasonPoster = null,
                episodeId = 1,
                episodeNumber = 1,
                episodeTitle = null,
                episodeLink = "stub",
                episodeState = EpisodeState.VIEWED,
                episodeDate = LocalDateTime.now()
            ), onFavoriteIconClick = {}, onViewedIconClick = {}
        )
    }
}
