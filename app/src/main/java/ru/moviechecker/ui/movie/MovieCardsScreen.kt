package ru.moviechecker.ui.movie

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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.core.content.ContextCompat
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
import ru.moviechecker.database.movies.MovieCardsView
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

object MoviesDestination : NavigationDestination {
    override val route = "movies"
    override val titleRes = R.string.movies_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MoviesScreen(
    modifier: Modifier = Modifier,
    viewModel: MoviesScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    val nonFavoritesVisibilityState = remember {
        mutableStateOf(true)
    }
    val viewedVisibilityState = remember {
        mutableStateOf(true)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CheckerTopAppBar(
                title = stringResource(MoviesDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                onFavoritesClicked = {
                    nonFavoritesVisibilityState.value = !nonFavoritesVisibilityState.value
                },
                onViewedClicked = {
                    viewedVisibilityState.value = !viewedVisibilityState.value
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
                workManager
                    .beginUniqueWork(
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
            }
        }

        val pullToRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            MovieList(
                movies = homeUiState.movies,
                showNonFavorites = nonFavoritesVisibilityState.value,
                showViewed = viewedVisibilityState.value,
                onClick = { viewModel.markEpisodeViewed(it) },
                onLongClick = {},
                onViewedIconClick = { viewModel.markEpisodeViewed(it) },
                onFavoriteIconClick = { viewModel.switchFavoritesMark(it) }
            )
        }
    }
}

@Composable
fun MovieList(
    movies: List<MovieCardsView>,
    showNonFavorites: Boolean,
    showViewed: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit = {},
    onLongClick: (Int) -> Unit = {},
    onViewedIconClick: (Int) -> Unit = {},
    onFavoriteIconClick: (Int) -> Unit = {}
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = movies, key = { listOf(it.id, it.seasonNumber) }) { movie ->
            AnimatedVisibility(
                visible = (showNonFavorites || movie.favoritesMark)
                        && (showViewed || !movie.viewedMark),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                MovieItem(
                    movie = movie,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onViewedIconClick = onViewedIconClick,
                    onFavoriteIconClick = onFavoriteIconClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieItem(
    movie: MovieCardsView,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit = {},
    onLongClick: (Int) -> Unit = {},
    onViewedIconClick: (Int) -> Unit = {},
    onFavoriteIconClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    movie.nextEpisodeId?.let { episodeId ->
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(movie.nextEpisodeLink.toString())
                        )
                        ContextCompat.startActivity(context, browserIntent, null)

                        onClick(episodeId)
                    }
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
                        movie.poster?.let { Poster(it) }
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
                            modifier = Modifier.clickable { onFavoriteIconClick(movie.id) },
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
                                id = movie.nextEpisodeId,
                                number = movie.nextEpisodeNumber,
                                title = movie.nextEpisodeTitle,
                                date = movie.nextEpisodeDate,
                                viewedMark = false,
                                style = MaterialTheme.typography.bodySmall,
                                onViewedIconClick = onViewedIconClick
                            )
                        }
                    }

                    movie.nextEpisodeNumber?.let { nextEpisodeNumber ->
                        if (movie.lastEpisodeNumber - nextEpisodeNumber > 1) {
                            HorizontalDivider()
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        EpisodeItem(
                            id = movie.lastEpisodeId,
                            number = movie.lastEpisodeNumber,
                            title = movie.lastEpisodeTitle,
                            date = movie.lastEpisodeDate,
                            viewedMark = movie.viewedMark,
                            style = MaterialTheme.typography.bodySmall,
                            onViewedIconClick = onViewedIconClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Poster(data: ByteArray) {
    val image = BitmapFactory.decodeByteArray(
        data,
        0,
        data.size
    )
        .asImageBitmap()
    Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(Icons.Default.Favorite.defaultWidth * 2)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun EpisodeItem(
    id: Int?,
    number: Int,
    title: String?,
    date: LocalDateTime?,
    viewedMark: Boolean,
    onViewedIconClick: (Int) -> Unit,
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
                id?.let(onViewedIconClick)
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
    } else if(tomorrow) {
        dateString = stringResource(
            R.string.tomorrow_time,
            date.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    } else if(today) {
        dateString = stringResource(
            R.string.today_time, date.format(
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            )
        )
    } else if(yesterday) {
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
    MoviecheckerTheme {
        MovieList(
            movies = listOf(
                MovieCardsView(
                    id = 1,
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
                MovieCardsView(
                    id = 2,
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
                MovieCardsView(
                    id = 3,
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
                MovieCardsView(
                    id = 2,
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
            showNonFavorites = true,
            showViewed = true
        )
    }
}
