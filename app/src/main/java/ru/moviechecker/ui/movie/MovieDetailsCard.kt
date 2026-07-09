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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ru.moviechecker.R
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.ui.theme.MoviecheckerTheme
import java.time.LocalDateTime

@Composable
fun MovieDetailsCard(
    movie: MovieDetailsCardModel,
    onFavoriteIconClick: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val poster = movie.poster?:movie.seasons.last().poster
        poster?.let {
            Poster(
                it,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
        }
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Icon(
                imageVector = if (movie.favoritesMark) ImageVector.vectorResource(
                    R.drawable.favorite_24px_filled
                ) else ImageVector.vectorResource(R.drawable.favorite_24px),
                contentDescription = null,
                modifier = Modifier.clickable { onFavoriteIconClick(movie.id) },
                tint = if (movie.favoritesMark) Color.Yellow else Color.Gray
            )
            Text(text = if (movie.favoritesMark) "В избранном" else "Добавить в избранные")
        }
    }
}

@Composable
fun MovieDetailsSeasonCard(
    season: SeasonCardModel,
    isExpanded: Boolean,
    onClickOnExpand: () -> Unit = {},
    onClickOnEpisode: (Int) -> Unit = {},
    onClickOnEpisodeViewed: (Int) -> Unit = {}
) {
    val rotation = animateFloatAsState(
        targetValue = if (isExpanded) 0f else 180f,
        label = "expand"
    )
    Card(
        onClick = { onClickOnExpand() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(dimensionResource(id = R.dimen.padding_small))) {
            season.poster?.let { Poster(it, modifier = Modifier.width(60.dp)) }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(id = R.dimen.padding_small))
            ) {
                season.title
                    ?.let { title ->
                        if (title.endsWith(season.number.toString())) {
                            Text(text = title)
                        } else {
                            Text(text = "$title ${season.number}")
                        }
                    }
                    ?: Text(
                        text = stringResource(R.string.season_title, season.number, "")
                    )
                Text(text = stringResource(R.string.episodes_with_count, season.episodes.size))
            }
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.keyboard_arrow_up_24px),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(rotationZ = rotation.value)
                    .align(alignment = CenterVertically)
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(animationSpec = tween())
        ) {
            Column {
                season.episodes.forEach { episode ->
                    MovieDetailsEpisodeCard(
                        episode = episode,
                        onClick = { },
                        onClickOnViewed = onClickOnEpisodeViewed
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDetailsEpisodeCard(
    episode: EpisodeCardModel,
    onClick: (Int) -> Unit = {},
    onClickOnViewed: (Int) -> Unit = {}
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(episode.id) }
    ) {
        Row {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.check_24px),
                contentDescription = null,
                modifier = Modifier.clickable {
                    onClickOnViewed(episode.id)
                },
                tint = if (episode.state == EpisodeState.VIEWED) Color.Green else Color.Gray
            )
            Text(
                text = episode.title?.let { title ->
                    stringResource(R.string.named_item_title, title, episode.number, "")
                } ?: stringResource(R.string.episode_title, episode.number, "")
            )
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
fun PreviewMovieDetailsCard(
    @PreviewParameter(MovieDetailsCardPreviewParameterProvider::class) favoritesMark: Boolean
) {
    MoviecheckerTheme {
        MovieDetailsCard(
            movie = MovieDetailsCardModel(
                id = 1,
                siteId = 1,
                pageId = "movie_page_id",
                title = "Фильм такой-то",
                poster = poster,
                favoritesMark = favoritesMark,
                seasons = listOf()
            )
        )
    }
}

class MovieDetailsCardPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@Preview("Movie details")
@Preview("Movie details (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewMovieDetailsSeasonCard() {
    MoviecheckerTheme {
        MovieDetailsSeasonCard(
            season = SeasonCardModel(
                id = 1,
                number = 1,
                poster = poster,
                episodes = listOf(
                    EpisodeCardModel(
                        id = 1,
                        number = 1,
                        link = "stub",
                        state = EpisodeState.VIEWED,
                        date = LocalDateTime.now()
                    ),
                    EpisodeCardModel(
                        id = 2,
                        number = 2,
                        link = "stub",
                        state = EpisodeState.RELEASED,
                        date = LocalDateTime.now()
                    ),
                    EpisodeCardModel(
                        id = 3,
                        number = 3,
                        link = "stub",
                        state = EpisodeState.EXPECTED,
                        date = LocalDateTime.now()
                    )
                )
            ),
            isExpanded = true
        )
    }
}