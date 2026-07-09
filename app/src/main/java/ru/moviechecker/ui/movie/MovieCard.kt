package ru.moviechecker.ui.movie

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.moviechecker.R
import ru.moviechecker.ui.theme.MoviecheckerTheme
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun MovieCard(
    cardProvider: () -> NewMovieCardModel,
    onClick: (Int) -> Unit = {},
    onClickOnFavorite: (Int) -> Unit = {},
    onClickOnViewed: (Int) -> Unit = {},
    onClickOnOpenInBrowser: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val card = cardProvider()

    Card(
        modifier = Modifier
//            .combinedClickable(
//                onClick = {
//                    val link = movie.nextEpisodeLink ?: movie.lastEpisodeLink
//                    val browserIntent = Intent(
//                        Intent.ACTION_VIEW,
//                        link.toString().toUri()
//                    )
//                    context.startActivity(browserIntent)

//                    onClick(movie)
//                },
//                onLongClick = { onLongClick(movie) }
//            )
            .padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(card.id) }
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            card.poster?.let {
                Poster(
                    it,
                    modifier = Modifier
                        .width(ImageVector.vectorResource(R.drawable.favorite_24px).defaultWidth * 2)
                )
            }

            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        modifier = Modifier.clickable { onClickOnFavorite(card.id) },
                        imageVector = if (card.favoritesMark) ImageVector.vectorResource(
                            R.drawable.favorite_24px_filled
                        ) else ImageVector.vectorResource(R.drawable.favorite_24px),
                        contentDescription = null,
                        tint = if (card.favoritesMark) Color.Yellow else Color.Gray
                    )

                    val lastSeason =
                        true // TODO: lastSeasonId == (nextSeasonId ?: lastSeasonId)
                    Text(
                        text = if (card.seasonNumber == 1) card.title else stringResource(
                            R.string.item_title,
                            card.title,
                            card.seasonNumber,
                            if (lastSeason) "" else "+"
                        ),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        modifier = Modifier.clickable {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                card.episode.link.toString().toUri()
                            )
                            context.startActivity(browserIntent)

                            onClickOnOpenInBrowser(card.episode.id)
                        },
                        imageVector = ImageVector.vectorResource(R.drawable.open_in_new_24px),
                        contentDescription = stringResource(R.string.open_next_or_last)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.clickable { onClickOnViewed(card.episode.id) },
                        imageVector = ImageVector.vectorResource(R.drawable.check_24px),
                        contentDescription = null,
                        tint = if (card.episode.viewedMark) Color.Green else Color.Gray
                    )

                    Text(
                        text = card.episode.title?.let { title ->
                            stringResource(
                                R.string.named_item_title,
                                title,
                                card.episode.number,
                                if (card.hasMoreEpisodes) "+" else ""
                            )
                        } ?: stringResource(
                            R.string.episode_title,
                            card.episode.number,
                            if (card.hasMoreEpisodes) "+" else ""
                        ),
                        modifier = Modifier.weight(1f),
                        color = if (!card.episode.viewedMark && (card.favoritesMark || card.episode.number == 1)) Color.Green else Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Date(
                        card.updatedAt,
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

@Preview
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MovieCardPreview(
    @PreviewParameter(MovieCardPreviewParameterProvider::class) movieCard: NewMovieCardModel
) {
    MoviecheckerTheme {
        MovieCard(
            cardProvider = { movieCard }
        )
    }
}

class MovieCardPreviewParameterProvider : PreviewParameterProvider<NewMovieCardModel> {
    override val values = sequenceOf(
        NewMovieCardModel(
            id = 1,
            title = "Начальное сегодня",
            poster = poster,
            favoritesMark = false,
            seasonNumber = 1,
            episode = NewEpisodeModel(
                id = 1,
                number = 1,
                title = "Серия №1",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = false
            ),
            hasMoreEpisodes = false,
            updatedAt = LocalDateTime.now()
        ),
        NewMovieCardModel(
            id = 2,
            title = "Следующий сегодня",
            poster = poster,
            favoritesMark = false,
            seasonNumber = 2,
            episode = NewEpisodeModel(
                id = 1,
                number = 2,
                title = "Следующий эпизод",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = true
            ),
            hasMoreEpisodes = true,
            updatedAt = LocalDateTime.now()
        ),
        NewMovieCardModel(
            id = 3,
            title = "В избранном и просмотрено вчера",
            poster = poster,
            favoritesMark = true,
            seasonNumber = 1,
            episode = NewEpisodeModel(
                id = 1,
                number = 2,
                title = "Следующий эпизод",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = true
            ),
            hasMoreEpisodes = false,
            updatedAt = LocalDateTime.now()
        ),
        NewMovieCardModel(
            id = 4,
            title = "В избранном и не просмотрено вчера",
            poster = poster,
            favoritesMark = true,
            seasonNumber = 1,
            episode = NewEpisodeModel(
                id = 1,
                number = 2,
                title = "Следующий эпизод",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = true
            ),
            hasMoreEpisodes = false,
            updatedAt = LocalDateTime.now()
        ),
        NewMovieCardModel(
            id = 5,
            title = "Очень длинное название, ну очень длинное или не очень позавчера+",
            poster = poster,
            favoritesMark = false,
            seasonNumber = 1,
            episode = NewEpisodeModel(
                id = 1,
                number = 2,
                title = "Следующий эпизод",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = false
            ),
            hasMoreEpisodes = false,
            updatedAt = LocalDateTime.now()
        )
    )
}