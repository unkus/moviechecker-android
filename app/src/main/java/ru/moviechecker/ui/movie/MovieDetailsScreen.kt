package ru.moviechecker.ui.movie

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.R
import ru.moviechecker.compose.LazyList
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.ui.AppViewModelProvider
import ru.moviechecker.ui.navigation.NavigationDestination
import ru.moviechecker.ui.theme.MoviecheckerTheme
import java.net.URI
import java.time.LocalDateTime

data object MovieDetailsDestination : NavigationDestination {
    override val route = "movie_details"
    override val titleRes = R.string.movie_details_title
    const val movieIdArg = "movieId"
    val routeWithArgs = "$route/{$movieIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    navigateToMovieEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val movieUiState = viewModel.movieUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        MovieDetails(
            movieUiState = movieUiState.value,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetails(
    movieUiState: MovieDetailsUiState,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    Column {
        val movie = movieUiState.movie
        val seasons = movieUiState.seasons
        movie.poster?.let { Poster(movie.poster) }
        Text(text = movie.title, style = style)
        Text(text = movie.pageId, style = style)

        LazyList(
            items = seasons,
            keyExtractor = { it.number },
            itemContent = {
                SeasonItem(
                    season = it,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    style = style
                )
            }
        )
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
private fun SeasonItem(
    season: SeasonModel,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    Card(
        modifier = modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            season.title?.let { title ->
                if (title.endsWith(season.number.toString())) {
                    Text(text = title, style = style)
                } else {
                    Text(text = "$title ${season.number}", style = style)
                }
            } ?: Text(text = "Сезон ${season.number}", style = style)

//            List(
//                items = season.episodes,
//                keyExtractor = { it.number },
//                itemContent = {
//                    EpisodeItem(
//                        episode = it,
//                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
//                        style = style
//                    )
//                },
//            )
        }
    }
}

@Composable
private fun EpisodeItem(
    episode: EpisodeModel,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    Card(
        modifier = modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
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

@Preview(locale = "ru-RU")
@Composable
fun MovieDetailsPreview() {
    MoviecheckerTheme {
        MovieDetails(
            movieUiState = MovieDetailsUiState(
                movie = MovieDetails(
                    site = URI.create(""),
                    title = "Movie",
                    pageId = "/movie",
                    link = "/movie",
                    favoritesMark = false
                ),
                seasons = listOf(
                    SeasonModel(
                        number = 1,
                        episodes = listOf(
                            EpisodeModel(
                                number = 1,
                                link = "/1",
                                state = EpisodeState.VIEWED,
                                date = LocalDateTime.now().minusWeeks(3)
                            ),
                            EpisodeModel(
                                number = 2,
                                link = "/2",
                                state = EpisodeState.RELEASED,
                                date = LocalDateTime.now().minusWeeks(2)
                            ),
                            EpisodeModel(
                                number = 3,
                                link = "/3",
                                state = EpisodeState.RELEASED,
                                date = LocalDateTime.now().minusWeeks(1)
                            )
                        )
                    ),
                    SeasonModel(
                        number = 2,
                        episodes = listOf(
                            EpisodeModel(
                                number = 1,
                                link = "/1",
                                state = EpisodeState.RELEASED,
                                date = LocalDateTime.now()
                            )
                        )
                    )
                )
            )
        )
    }
}
