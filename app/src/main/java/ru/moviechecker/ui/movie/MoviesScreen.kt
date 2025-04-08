package ru.moviechecker.ui.movie

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import ru.moviechecker.R
import ru.moviechecker.ui.theme.CheckerTheme
import ru.moviechecker.workers.AsyncCleanupDataWorker
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    uiState: MoviesUiState,
    moviesProvider: () -> List<MovieCardModel>,
    openDrawer: () -> Unit,
    onRefresh: () -> Unit,
    onShouldShowOnlyFavoritesIconClick: () -> Unit,
    onShouldShowViewedEpisodesIconClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onMovieLongClick: (Int) -> Unit,
    onFavoriteIconClick: (Int) -> Unit,
    onViewedIconClick: (Int) -> Unit,
    navigateBack: () -> Unit = {}
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MoviesTopAppBar(
                isBackable = uiState.siteId != null,
                shouldShowOnlyFavorites = uiState.shouldShowOnlyFavorites,
                shouldShowViewedEpisodes = uiState.shouldShowViewedEpisodes,
                openDrawer = openDrawer,
                navigateBack = navigateBack,
                topAppBarState = topAppBarState,
                onShouldShowOnlyFavoritesClick = onShouldShowOnlyFavoritesIconClick,
                onShouldShowViewedEpisodesClick = onShouldShowViewedEpisodesIconClick
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
        val refreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            state = refreshState,
            indicator = {
                Indicator(
                    modifier = Modifier
                        .padding(innerPadding)
                        .align(Alignment.TopCenter),
                    isRefreshing = uiState.isLoading,
                    state = refreshState
                )
            }
        ) {
            MovieList(
                movies = moviesProvider(),
                shouldShowOnlyFavorites = uiState.shouldShowOnlyFavorites,
                shouldShowViewedEpisodes = uiState.shouldShowViewedEpisodes,
                modifier = Modifier.padding(innerPadding),
                onMovieClick = { id -> onMovieClick(id) },
                onMovieLongClick = onMovieLongClick,
                onFavoritesIconClick = onFavoriteIconClick,
                onEpisodeViewedIconClick = onViewedIconClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesTopAppBar(
    isBackable: Boolean,
    shouldShowOnlyFavorites: Boolean,
    shouldShowViewedEpisodes: Boolean,
    openDrawer: () -> Unit,
    navigateBack: () -> Unit = {},
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
    onShouldShowOnlyFavoritesClick: () -> Unit,
    onShouldShowViewedEpisodesClick: () -> Unit,
) {
    val context = LocalContext.current
    val title = stringResource(id = R.string.movies_title)
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(title)
        },
        navigationIcon = {
            if (isBackable) {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back)
                    )
                }
            } else {
                IconButton(onClick = openDrawer) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.cd_open_navigation_drawer)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onShouldShowOnlyFavoritesClick) {
                Icon(
                    imageVector = if (shouldShowOnlyFavorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    tint = if (shouldShowOnlyFavorites) Color.Yellow else Color.Gray,
                    contentDescription = stringResource(R.string.cd_favorites_filter)
                )
            }
            IconButton(onClick = onShouldShowViewedEpisodesClick) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    tint = if (shouldShowViewedEpisodes) Color.Green else Color.Gray,
                    contentDescription = stringResource(R.string.cd_viewed_filter)
                )
            }
            IconButton(onClick = {
                Toast.makeText(
                    context,
                    "Search is not yet implemented in this configuration",
                    Toast.LENGTH_LONG
                ).show()
            }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun MovieList(
    movies: List<MovieCardModel>,
    shouldShowOnlyFavorites: Boolean,
    shouldShowViewedEpisodes: Boolean,
    modifier: Modifier = Modifier,
    onMovieClick: (Int) -> Unit,
    onMovieLongClick: (Int) -> Unit,
    onFavoritesIconClick: (Int) -> Unit,
    onEpisodeViewedIconClick: (Int) -> Unit,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = movies, key = { listOf(it.id, it.seasonNumber) }) { movie ->
            AnimatedVisibility(
                visible = (!shouldShowOnlyFavorites || movie.favoritesMark)
                        && (shouldShowViewedEpisodes || !movie.viewedMark),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MovieItem(
                    movie = movie,
                    onClick = onMovieClick,
                    onLongClick = onMovieLongClick,
                    onFavoritesIconClick = onFavoritesIconClick,
                    onEpisodeViewedIconClick = onEpisodeViewedIconClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieItem(
    movie: MovieCardModel,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onFavoritesIconClick: (Int) -> Unit,
    onEpisodeViewedIconClick: (Int) -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    val link = movie.nextEpisodeLink ?: movie.lastEpisodeLink
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        link.toString().toUri()
                    )
                    context.startActivity(browserIntent)

                    onClick(movie.nextEpisodeId ?: movie.lastEpisodeId)
                },
                onLongClick = { onLongClick(movie.id) }
            )
            .padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(
                        id = R.dimen.padding_small
                    )
                )
            ) {
                Column(modifier = Modifier.wrapContentWidth()) {
                    Row {
                        movie.poster?.let {
                            Poster(
                                it,
                                modifier = Modifier.width(Icons.Default.Favorite.defaultWidth * 2)
                            )
                        }
                    }
                }
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = if (movie.favoritesMark) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.clickable { onFavoritesIconClick(movie.id) },
                            tint = if (movie.favoritesMark) Color.Yellow else Color.Gray
                        )
                        Text(
                            text = movie.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    movie.nextEpisodeNumber?.let { nextEpisodeNumber ->
                        if (nextEpisodeNumber < movie.lastEpisodeNumber) {
                            EpisodeItem(
                                id = movie.nextEpisodeId!!,
                                number = movie.nextEpisodeNumber,
                                title = movie.nextEpisodeTitle,
                                date = movie.nextEpisodeDate,
                                viewedMark = false,
                                onEpisodeViewedIconClick = onEpisodeViewedIconClick,
                                style = MaterialTheme.typography.bodySmall
                            )
                            HorizontalDivider()
                        }
                    }

                    EpisodeItem(
                        id = movie.lastEpisodeId,
                        number = movie.lastEpisodeNumber,
                        title = movie.lastEpisodeTitle,
                        date = movie.lastEpisodeDate,
                        viewedMark = movie.viewedMark,
                        onEpisodeViewedIconClick = onEpisodeViewedIconClick,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun Poster(
    data: ByteArray,
    modifier: Modifier = Modifier
) {
    val image = try {
        BitmapFactory.decodeByteArray(
            data,
            0,
            data.size
        )
            .asImageBitmap()
    } catch (exception: Exception) {
        return
    }
    Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun EpisodeItem(
    id: Int,
    number: Int,
    title: String?,
    date: LocalDateTime?,
    viewedMark: Boolean,
    onEpisodeViewedIconClick: (Int) -> Unit,
    style: TextStyle = LocalTextStyle.current
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.clickable {
                onEpisodeViewedIconClick(id)
            },
            tint = if (viewedMark) Color.Green else Color.Gray
        )
        title?.let { title ->
            Text(
                text = stringResource(
                    R.string.episode_serial_number_titled, title,
                    number
                ),
                modifier = Modifier.weight(1f),
                color = if (number == 1) Color.Green else Color.Gray,
                style = style
            )
        } ?: Text(
            text = stringResource(R.string.episode_serial_number, number),
            modifier = Modifier.weight(1f),
            color = if (number == 1) Color.Green else Color.Gray,
            style = style
        )

        date?.let {
            Date(
                date,
                style = style
            )
        }
    }
}

@Composable
fun Date(
    date: LocalDateTime,
    style: TextStyle
) {
    val future = date.isAfter(LocalDate.now().plusDays(2).atStartOfDay())
    val tomorrow = !future && date.isAfter(LocalDate.now().plusDays(1).atStartOfDay())
    val today = !tomorrow && date.isAfter(LocalDate.now().atStartOfDay())
    val yesterday = !today && date.isAfter(LocalDate.now().minusDays(1).atStartOfDay())

    val dateString: String
    if (future) {
        dateString =
            date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
    } else if (tomorrow) {
        dateString = stringResource(
            R.string.tomorrow_time,
            date.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    } else if (today) {
        dateString = stringResource(
            R.string.today_time, date.format(
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            )
        )
    } else if (yesterday) {
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
        style = style,
        maxLines = 1
    )
}

@Preview(locale = "ru-RU")
@Composable
fun MovieListPreview() {
    CheckerTheme {
        MovieList(
            movies = listOf(
                MovieCardModel(
                    id = 1,
                    seasonId = 1,
                    seasonNumber = 1,
                    title = "Некоторый сериал с длинным названием",
                    favoritesMark = false,
                    nextEpisodeNumber = 1,
                    nextEpisodeDate = LocalDateTime.now(),
                    nextEpisodeTitle = "Некоторый эпизод с длинным названием",
                    lastEpisodeId = 1, // stub
                    lastEpisodeNumber = 1,
                    lastEpisodeTitle = "Некоторый эпизод с длинным названием",
                    lastEpisodeDate = LocalDateTime.now(),
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = false
                ),
                MovieCardModel(
                    id = 2,
                    seasonId = 2,
                    seasonNumber = 2,
                    title = "Сериал 2",
                    favoritesMark = true,
                    nextEpisodeNumber = 1,
                    nextEpisodeDate = LocalDateTime.now().minusDays(6),
                    nextEpisodeTitle = "Следующий эпизод",
                    lastEpisodeId = 1, // stub
                    lastEpisodeNumber = 2,
                    lastEpisodeDate = LocalDateTime.now().minusDays(5),
                    lastEpisodeTitle = "Последний эпизод",
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = false
                ),
                MovieCardModel(
                    id = 3,
                    seasonId = 3,
                    seasonNumber = 1,
                    title = "Сериал 3",
                    favoritesMark = true,
                    nextEpisodeNumber = 1,
                    nextEpisodeDate = LocalDateTime.now().minusDays(6),
                    nextEpisodeTitle = "Следующий эпизод",
                    lastEpisodeId = 1, // stub
                    lastEpisodeNumber = 3,
                    lastEpisodeDate = LocalDateTime.now().minusDays(4),
                    lastEpisodeTitle = "Последний эпизод",
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = false
                ),
                MovieCardModel(
                    id = 2,
                    seasonId = 1,
                    seasonNumber = 1,
                    title = "Просмотренный сериал",
                    favoritesMark = true,
                    lastEpisodeId = 1, // stub
                    lastEpisodeNumber = 10,
                    lastEpisodeTitle = "Последний просмотренный эпизод",
                    lastEpisodeDate = LocalDateTime.now().minusDays(15),
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = true
                )
            ),
            shouldShowOnlyFavorites = false,
            shouldShowViewedEpisodes = false,
            onMovieClick = {},
            onMovieLongClick = {},
            onFavoritesIconClick = {},
            onEpisodeViewedIconClick = {}
        )
    }
}
