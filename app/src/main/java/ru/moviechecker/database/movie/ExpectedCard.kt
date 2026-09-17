package ru.moviechecker.database.movie

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import java.time.LocalDateTime

data class ExpectedCard(
    val id: Int,
    val title: String,
    @ColumnInfo(name = "favorites_mark")
    val favoritesMark: Boolean,
    val poster: ByteArray?,
    val kinopoiskId: String?,

    @Embedded(prefix = "site_")
    val site: ExpectedCardSite,
    @Embedded(prefix = "movie_")
    val movie: ExpectedCardMovie,
    // первый не просмотренный или последний просмотренный сезон
    @Embedded(prefix = "season_")
    val season: ExpectedCardSeason,
    // первый не просмотренный или последний просмотренный эпизод
    @Embedded(prefix = "episode_")
    val episode: ExpectedCardEpisode
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpectedCard

        if (id != other.id) return false
        if (favoritesMark != other.favoritesMark) return false
        if (title != other.title) return false
        if (!poster.contentEquals(other.poster)) return false
        if (kinopoiskId != other.kinopoiskId) return false
        if (site != other.site) return false
        if (movie != other.movie) return false
        if (season != other.season) return false
        if (episode != other.episode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + favoritesMark.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + (kinopoiskId?.hashCode() ?: 0)
        result = 31 * result + site.hashCode()
        result = 31 * result + movie.hashCode()
        result = 31 * result + season.hashCode()
        result = 31 * result + episode.hashCode()
        return result
    }
}

data class ExpectedCardSite(
    val id: Int, // для фильтра по сайту
    val address: String, // для формирования ссылки
    @ColumnInfo(name = "use_mirror")
    val useMirror: Boolean, // для формирования ссылки
    val mirror: String?, // для формирования ссылки
)

data class ExpectedCardMovie(
    val id: Int, // для добавления/удаления в/из избранного
//    val title: String, // для отображения если нет названия у сезона
//    val poster: ByteArray?, // для отображения если нет постера у сезона
//    @ColumnInfo(name = "favorites_mark")
//    val favoritesMark: Boolean, // для отображения и фильтра
)

data class ExpectedCardSeason(
    val id: Int, // возможно для уникального ключа в списке, но это не точно
    val number: Int, // для отображения если нет названия
    val title: String? // для отображения
)

data class ExpectedCardEpisode(
    val id: Int, // для проставления статуса
    val number: Int, // для отображения
    val title: String?, // для отображения
    val date: LocalDateTime, // для отображения
    val link: String // для перехода в браузер
)