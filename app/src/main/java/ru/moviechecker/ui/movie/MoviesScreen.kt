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
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
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
    onMovieClick: (movieId: Int) -> Unit,
    onMovieLongClick: (movieId: Int) -> Unit,
    onFavoriteIconClick: (movieId: Int) -> Unit,
    onViewedIconClick: (episodeId: Int) -> Unit,
    navigateBack: () -> Unit = {}
) {
    val topAppBarState = rememberTopAppBarState()
    val refreshState = rememberPullToRefreshState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MoviesTopAppBar(
                siteTitle = uiState.siteTitle,
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
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            state = refreshState,
            modifier = Modifier.padding(innerPadding)
        ) {
            MovieList(
                movies = moviesProvider(),
                shouldShowOnlyFavorites = uiState.shouldShowOnlyFavorites,
                shouldShowViewedEpisodes = uiState.shouldShowViewedEpisodes,
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
    siteTitle: String?,
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
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                siteTitle?.let {
                    Text(text = siteTitle, style = MaterialTheme.typography.titleSmall)
                }
            }
        },
        navigationIcon = {
            siteTitle?.let {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back)
                    )
                }
            } ?: IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer)
                )
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
    onMovieClick: (movieId: Int) -> Unit,
    onMovieLongClick: (movieId: Int) -> Unit,
    onFavoritesIconClick: (movieId: Int) -> Unit,
    onEpisodeViewedIconClick: (episodeId: Int) -> Unit,
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

@Composable
fun MovieItem(
    movie: MovieCardModel,
    modifier: Modifier = Modifier,
    onClick: (movieId: Int) -> Unit,
    onLongClick: (movieId: Int) -> Unit,
    onFavoritesIconClick: (movieId: Int) -> Unit,
    onEpisodeViewedIconClick: (episodeId: Int) -> Unit,
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
                        val lastSeason = true // TODO: lastSeasonId == (nextSeasonId ?: lastSeasonId)
                        Text(
                            text = if (movie.seasonNumber == 1) movie.title else stringResource(R.string.item_title, movie.title, movie.seasonNumber, if (lastSeason) "" else "+"),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val highlighted = movie.nextEpisodeId?.let {
                        if (movie.favoritesMark) {
                            !movie.viewedMark
                        } else {
                            movie.nextEpisodeNumber == 1
                        }
                    } ?: false
                    EpisodeItem(
                        id = movie.nextEpisodeId ?: movie.lastEpisodeId,
                        number = movie.nextEpisodeNumber ?: movie.lastEpisodeNumber,
                        title = movie.nextEpisodeTitle ?: movie.lastEpisodeTitle,
                        date = movie.lastEpisodeDate,
                        last = movie.lastEpisodeNumber == (movie.nextEpisodeNumber
                            ?: movie.lastEpisodeNumber),
                        highlighted = highlighted,
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
    date: LocalDateTime,
    last: Boolean = true,
    highlighted: Boolean = false,
    viewedMark: Boolean,
    onEpisodeViewedIconClick: (episodeId: Int) -> Unit,
    style: TextStyle = LocalTextStyle.current
) {
    val text = title?.let {
        stringResource(R.string.named_item_title, title, number, if (last) "" else "+")
    } ?: stringResource(R.string.episode_title, number, if (last) "" else "+")

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

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = if (highlighted) Color.Green else Color.Gray,
            style = style
        )

        Date(
            date,
            style = style
        )
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
                    nextEpisodeId = 1,
                    nextEpisodeNumber = 1,
                    nextEpisodeDate = LocalDateTime.now(),
                    nextEpisodeTitle = "Некоторый эпизод с длинным названием",
                    lastEpisodeId = 1,
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
                    title = "Все новые серии в избранном подсвечены",
                    favoritesMark = true,
                    nextEpisodeId = 2,
                    nextEpisodeNumber = 2,
                    nextEpisodeDate = LocalDateTime.now().minusDays(5),
                    nextEpisodeTitle = "Последний эпизод",
                    lastEpisodeId = 2,
                    lastEpisodeNumber = 2,
                    lastEpisodeDate = LocalDateTime.now().minusDays(5),
                    lastEpisodeTitle = "Последний эпизод",
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = false
                ),
                MovieCardModel(
                    id = 3,
                    seasonId = 3,
                    seasonNumber = 2,
                    title = "Все новые серии в избранном подсвечены",
                    favoritesMark = true,
                    nextEpisodeId = 2,
                    nextEpisodeNumber = 2,
                    nextEpisodeDate = LocalDateTime.now().minusDays(4),
                    lastEpisodeId = 2,
                    lastEpisodeNumber = 2,
                    lastEpisodeDate = LocalDateTime.now().minusDays(4),
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = true
                ),
                MovieCardModel(
                    id = 4,
                    seasonId = 4,
                    seasonNumber = 1,
                    title = "Только первая серия не в избранном подсвечена",
                    favoritesMark = false,
                    nextEpisodeId = 1,
                    nextEpisodeNumber = 1,
                    nextEpisodeDate = LocalDateTime.now().minusDays(6),
                    lastEpisodeId = 3,
                    lastEpisodeNumber = 3,
                    lastEpisodeDate = LocalDateTime.now().minusDays(4),
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = false
                ),
                MovieCardModel(
                    id = 5,
                    seasonId = 5,
                    seasonNumber = 1,
                    title = "Только первая серия не в избранном подсвечена",
                    favoritesMark = false,
                    nextEpisodeId = 1,
                    nextEpisodeNumber = 2,
                    nextEpisodeDate = LocalDateTime.now().minusDays(6),
                    lastEpisodeId = 3,
                    lastEpisodeNumber = 3,
                    lastEpisodeDate = LocalDateTime.now().minusDays(4),
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = false
                ),
                MovieCardModel(
                    id = 6,
                    seasonId = 6,
                    seasonNumber = 1,
                    title = "Просмотренный сериал",
                    favoritesMark = false,
                    lastEpisodeId = 1,
                    lastEpisodeNumber = 10,
                    lastEpisodeTitle = "Последний просмотренный эпизод",
                    lastEpisodeDate = LocalDateTime.now().minusDays(15),
                    lastEpisodeLink = URI.create("stub"),
                    viewedMark = true
                )
            ),
            shouldShowOnlyFavorites = false,
            shouldShowViewedEpisodes = true,
            onMovieClick = {},
            onMovieLongClick = {},
            onFavoritesIconClick = {},
            onEpisodeViewedIconClick = {}
        )
    }
}
