package ru.moviechecker.ui.movie

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.moviechecker.R
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.ui.theme.CheckerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsUiState,
    movieProvider: () -> MovieModel?,
    seasonsProvider: () -> List<SeasonModel>,
    onRefresh: () -> Unit = {},
    onFavoriteIconClick: (movieId: Int) -> Unit = {},
    onSeasonExpanded: (number: Int) -> Unit = {},
    onEpisodeClick: (episodeId: Int) -> Unit = {},
    onEpisodeViewedMarkIconClick: (episodeId: Int) -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MovieDetailsTopAppBar(
                topAppBarState = topAppBarState,
                navigateBack = navigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            movieProvider()?.let { movie ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_small)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                        ) {
                            Poster(
                                movie.poster,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(CenterHorizontally)
                            )
                            Row {
                                Icon(
                                    imageVector = if (movie.favoritesMark) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.clickable { onFavoriteIconClick(movie.id) },
                                    tint = if (movie.favoritesMark) Color.Yellow else Color.Gray
                                )
                                Text(text = movie.title)
                            }
                            Text(text = stringResource(R.string.sites_title, movie.site))
                        }
                    }
                }
            }
            items(items = seasonsProvider(), key = { listOf(it.number) }) { season ->
                val rotation = animateFloatAsState(
                    targetValue = if (uiState.expandedSeasonNumber == season.number) 180f else 0f,
                    label = "expand"
                )
                Card(
                    onClick = { onSeasonExpanded(season.number) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(
                                id = R.dimen.padding_small
                            )
                        )
                    ) {
                        season.poster?.let {
                            Poster(
                                it,
                                modifier = Modifier.width(60.dp)
                            )
                        }
                        Column {
                            season.title?.let { title ->
                                if (title.endsWith(season.number.toString())) {
                                    Text(text = title)
                                } else {
                                    Text(text = "$title ${season.number}")
                                }
                            } ?: Text(text = stringResource(R.string.season_title, season.number, ""))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Серий: ${season.episodes.size}",
                                    modifier = Modifier.weight(1f)
                                )
                                Image(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(CenterVertically)
                                        .graphicsLayer(
                                            rotationZ = rotation.value
                                        )
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = uiState.expandedSeasonNumber == season.number,
                        enter = expandVertically(expandFrom = Alignment.Top),
                        exit = shrinkVertically(animationSpec = tween())
                    ) {
                        Column {
                            season.episodes.forEach { episode ->
                                EpisodeItem(
                                    episode = episode,
                                    onClick = onEpisodeClick,
                                    onViewedMarkIconClick = onEpisodeViewedMarkIconClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieDetailsTopAppBar(
    navigateBack: () -> Unit = {},
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
) {
    val title = stringResource(id = R.string.movie_details_title)
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun EpisodeItem(
    episode: EpisodeModel,
    onClick: (episodeId: Int) -> Unit = {},
    onViewedMarkIconClick: (episodeId: Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small))
            .fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(episode.id) }
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onViewedMarkIconClick(episode.id)
                },
                tint = if (episode.state == EpisodeState.VIEWED) Color.Green else Color.Gray
            )
            episode.title?.let { title ->
                if (title.endsWith(episode.number.toString())) {
                    Text(text = title)
                } else {
                    Text(text = "$title ${episode.number}")
                }
            } ?: Text(text = "Серия ${episode.number}")
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

@Preview("Movie details")
@Preview("Movie details (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewMovieDetailsScreen() {
    CheckerTheme {
//        MovieDetailsScreen(
//            uiState = MovieDetailsUiState(
//                movie = MovieModel(
//                    id = 1,
//                    site = URI.create("movie.site"),
//                    pageId = "movie_1",
//                    title = "Movie 1",
//                    favoritesMark = true
//                ),
//                seasons = List
//            )
//        )
    }
}
