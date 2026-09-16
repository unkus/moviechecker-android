package ru.moviechecker.ui.movie

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.moviechecker.R
import ru.moviechecker.ui.common.openInBrowser
import ru.moviechecker.ui.common.openInKinopoisk
import ru.moviechecker.ui.theme.MoviecheckerTheme
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun MovieCardContent(
    dataProvider: () -> MovieCardModel,
    onClickOnFavorite: (Int) -> Unit = {},
    onClickOnViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onActionPerformed: (Int) -> Unit = {}
) {
    val movie = dataProvider()
    Row(
        modifier = Modifier
            .padding(4.dp)
            .height(IntrinsicSize.Max)
    ) {
        movie.poster?.let {
            Poster(
                data = it,
                modifier = Modifier.fillMaxHeight()
            )
        }

        DataSection(
            card = movie, modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClickOnFavorite = onClickOnFavorite,
            onClickOnViewed = onClickOnViewed,
            onActionPerformed = onActionPerformed
        )
    }
}

@Composable
fun DataSection(
    card: MovieCardModel,
    modifier: Modifier = Modifier,
    onClickOnFavorite: (Int) -> Unit = {},
    onClickOnViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onActionPerformed: (Int) -> Unit = {}
) {
    val lastSeason =
        true // TODO: lastSeasonId == (nextSeasonId ?: lastSeasonId)
    val title = card.title +
            (if (card.season.number > 1) " (${card.season.number}${if (lastSeason) "" else "+"})" else "") +
            (card.season.title?.let { ": $it" } ?: "")

    Column(
        modifier = modifier
    ) {
        Row {
            Icon(
                modifier = Modifier.clickable { onClickOnFavorite(card.id) },
                imageVector = if (card.favoritesMark) ImageVector.vectorResource(
                    R.drawable.favorite_24px_filled
                ) else ImageVector.vectorResource(R.drawable.favorite_24px),
                contentDescription = null,
                tint = if (card.favoritesMark) Color.Yellow else Color.Gray
            )

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ActionSection(card = card, onActionPerformed = onActionPerformed)
        }

        Row {
            Icon(
                modifier = Modifier.clickable {
                    onClickOnViewed(
                        card.episode.id,
                        card.episode.viewedMark
                    )
                },
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

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                DateSection(card.updatedAt)
            }
        }
    }
}

@Composable
fun ActionSection(
    card: MovieCardModel,
    modifier: Modifier = Modifier,
    onActionPerformed: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.clickable {
                openInBrowser(context, card.episode.link.toString().toUri())
                onActionPerformed(card.episode.id)
            },
            imageVector = ImageVector.vectorResource(R.drawable.open_in_new_24px),
            contentDescription = stringResource(R.string.open_next_or_last)
        )

        card.kinopoiskId?.let {
            Icon(
                modifier = Modifier.clickable {
                    openInKinopoisk(context, card)
                    onActionPerformed(card.episode.id)
                },
                imageVector = ImageVector.vectorResource(R.drawable.open_in_new_24px),
                contentDescription = stringResource(R.string.open_next_or_last)
            )
        }
    }
}

@Composable
fun DateSection(
    date: LocalDateTime
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
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1
    )
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
            .wrapContentHeight()
            .width(ImageVector.vectorResource(R.drawable.favorite_24px).defaultWidth * 2)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Preview(name = "Карточка фильма")
@Preview(name = "Карточка фильма (темная тема)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MovieCardPreview(
    @PreviewParameter(MovieCardPreviewParameterProvider::class) movieCard: MovieCardModel
) {
    MoviecheckerTheme {
        MovieCardContent(
            dataProvider = { movieCard }
        )
    }
}

class MovieCardPreviewParameterProvider : PreviewParameterProvider<MovieCardModel> {
    override val values = sequenceOf(
        MovieCardModel(
            id = 1,
            title = "Начальное сегодня",
            poster = poster,
            favoritesMark = false,
            kinopoiskId = "id",
            season = SeasonModel(1),
            episode = EpisodeModel(
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
        MovieCardModel(
            id = 2,
            title = "Следующий сегодня",
            poster = poster,
            favoritesMark = false,
            season = SeasonModel(2, "Подзаголовок"),
            episode = EpisodeModel(
                id = 1,
                number = 2,
                title = "Следующий эпизод",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = true
            ),
            hasMoreEpisodes = true,
            updatedAt = LocalDateTime.now().minusDays(1)
        ),
        MovieCardModel(
            id = 3,
            title = "В избранном и просмотрено вчера",
            poster = poster,
            favoritesMark = true,
            season = SeasonModel(1),
            episode = EpisodeModel(
                id = 1,
                number = 2,
                title = "Следующий эпизод",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = true
            ),
            hasMoreEpisodes = false,
            updatedAt = LocalDateTime.now().minusDays(2)
        ),
        MovieCardModel(
            id = 4,
            title = "В избранном и не просмотрено вчера",
            poster = poster,
            favoritesMark = true,
            season = SeasonModel(1),
            episode = EpisodeModel(
                id = 1,
                number = 2,
                title = "Следующий эпизод",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = true
            ),
            hasMoreEpisodes = false,
            updatedAt = LocalDateTime.now().minusDays(3)
        ),
        MovieCardModel(
            id = 5,
            title = "Очень длинное название, ну очень длинное или не очень позавчера+",
            poster = poster,
            favoritesMark = false,
            season = SeasonModel(1),
            episode = EpisodeModel(
                id = 1,
                number = 2,
                title = "Длинное название эпизода очень длинное",
                link = URI.create("stub"),
                date = LocalDateTime.now(),
                viewedMark = false
            ),
            hasMoreEpisodes = false,
            updatedAt = LocalDateTime.now().minusDays(4)
        )
    )
}