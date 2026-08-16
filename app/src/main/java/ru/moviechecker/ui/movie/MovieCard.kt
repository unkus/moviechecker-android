package ru.moviechecker.ui.movie

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.dimensionResource
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
import ru.moviechecker.ui.theme.MoviecheckerTheme
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun MovieCard(
    cardProvider: () -> MovieCardModel,
    onClick: (Int) -> Unit = {},
    onClickOnFavorite: (Int) -> Unit = {},
    onClickOnViewed: (Int, Boolean) -> Unit = { id, isViewed -> }
) {
    val card = cardProvider()

    Card(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(card.id) }
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .height(IntrinsicSize.Max)
        ) {
            card.poster?.let {
                Poster(
                    data = it,
                    modifier = Modifier.fillMaxHeight()
                )
            }

            DataSection(
                card = card, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClickOnFavorite = onClickOnFavorite,
                onClickOnViewed = onClickOnViewed
            )
        }
    }
}

@Composable
fun DataSection(
    card: MovieCardModel,
    modifier: Modifier = Modifier,
    onClickOnFavorite: (Int) -> Unit = {},
    onClickOnViewed: (Int, Boolean) -> Unit = { id, isViewed -> }
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

            ActionSection(card = card)
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
                openInBrowser(context, card)
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

fun openInBrowser(context: Context, card: MovieCardModel) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = card.episode.link.toString().toUri()

    startActivity(context, intent)
}

fun openInKinopoisk(context: Context, card: MovieCardModel) {
    val intent = Intent(Intent.ACTION_VIEW)
    val packageName = "ru.kinopoisk" // Пакетное имя приложения «Кинопоиск»

    // Проверяем, установлено ли приложение
    try {
        val packageManager = context.packageManager
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))

        // Приложение установлено — указываем его явно
        intent.setPackage(packageName)
        // TODO: разобраться с открытием приложения -
        //  открытие через hd.kinopoisk.ru не происходит
        intent.data = "https://kinopoisk.ru/film/${card.kinopoiskId}".toUri()
    } catch (e: PackageManager.NameNotFoundException) {
        // Приложение не установлено — оставляем intent без setPackage, откроется браузер
        intent.data =
            "https://hd.kinopoisk.ru/film/${card.kinopoiskId}?content_tab=series&season=${card.season.number}&episode=${card.episode.number}&watch=".toUri()
    }

    startActivity(context, intent)
}

fun startActivity(context: Context, intent: Intent) {

    // Добавляем флаги для корректной работы
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Если не удалось запустить ни приложение, ни браузер
        Toast.makeText(context, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
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
        MovieCard(
            cardProvider = { movieCard }
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