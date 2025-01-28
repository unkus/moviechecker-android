package ru.moviechecker.ui.movie

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.moviechecker.CheckerTopAppBar
import ru.moviechecker.R
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.ui.AppViewModelProvider
import ru.moviechecker.ui.episode.EpisodeDetailsDestination
import ru.moviechecker.ui.navigation.NavigationDestination
import ru.moviechecker.ui.theme.MoviecheckerTheme

data object MovieDetailsDestination : NavigationDestination {
    override val route = "movie_details"
    override val titleRes = R.string.movie_details_title
    const val ID_ARG = "movieId"
    val routeWithArgs = "$route/{$ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    navigateToMovieEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: MovieDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            CheckerTopAppBar(
                title = stringResource(EpisodeDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToMovieEdit(viewModel.movieId) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))

            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_movie_title),
                )
            }
        },
        modifier = Modifier
    ) { innerPadding ->
        MovieDetailsBody(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
private fun MovieDetailsBody(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val seasonListUiState = viewModel.seasonListUiState.collectAsState()
    var expandedItem by remember { mutableIntStateOf(0) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
    ) {
        item { MovieData() }
        items(
            items = seasonListUiState.value.seasons,
            key = { listOf(it.number) }) { season ->
            SeasonItem(
                season = season,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                expanded = expandedItem == season.number,
                onItemClick = {
                    expandedItem = if (expandedItem == season.number) {
                        0
                    } else {
                        season.number
                    }
                    coroutineScope.launch {
                        listState.animateScrollToItem(season.number, scrollOffset = 10)
                    }
                }
            )
        }

    }
}

@Composable
private fun MovieData(
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    viewModel: MovieDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val movieUiState = viewModel.movieDataUiState.collectAsState()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            movieUiState.value.movie.poster?.let {
                Poster(
                    it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(CenterHorizontally)
                )
            }
            Row {
                Icon(
                    imageVector = if (movieUiState.value.movie.favoritesMark) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.clickable { viewModel.switchFavoritesMark(movieUiState.value.movie.id) },
                    tint = if (movieUiState.value.movie.favoritesMark) Color.Yellow else Color.Gray
                )
                Text(text = movieUiState.value.movie.title, style = style)
            }
            Text(text = "Сайт: ${movieUiState.value.movie.site}", style = style)
        }
    }
}

@Composable
private fun SeasonItem(
    season: SeasonModel,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onItemClick: () -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val rotation = animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "expand"
    )
    Card(
        modifier = modifier
            .clickable { onItemClick() },
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
                        Text(text = title, style = style)
                    } else {
                        Text(text = "$title ${season.number}", style = style)
                    }
                } ?: Text(text = "Сезон ${season.number}", style = style)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Серий: ${season.episodes.size}",
                        modifier = Modifier.weight(1f),
                        style = style
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
        AnimatedVisibility(expanded) {
            EpisodeList(
                episodes = season.episodes
            )
        }
    }
}

@Composable
private fun EpisodeList(
    episodes: List<EpisodeModel>,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    Column(modifier = modifier) {
//        items(items = episodes, key = { listOf(it.id, it.number) }) { episode ->
//            EpisodeItem(
//                episode = episode,
//                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
//                style = style
//            )
//        }
        episodes.forEach { episode ->
            EpisodeItem(
                episode = episode,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                style = style
            )
        }
    }
}

@Composable
private fun EpisodeItem(
    episode: EpisodeModel,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    viewModel: MovieDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Card(
        modifier = modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.clickable {
                    viewModel.switchEpisodeViewedMark(episode.id)
                },
                tint = if (episode.state == EpisodeState.VIEWED) Color.Green else Color.Gray
            )
            episode.title?.let { title ->
                if (title.endsWith(episode.number.toString())) {
                    Text(text = title, style = style)
                } else {
                    Text(text = "$title ${episode.number}", style = style)
                }
            } ?: Text(text = "Серия ${episode.number}", style = style)
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
