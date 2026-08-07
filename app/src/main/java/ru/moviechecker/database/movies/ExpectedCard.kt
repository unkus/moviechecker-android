package ru.moviechecker.database.movies

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.net.URI
import java.time.LocalDateTime

data class ExpectedCard(
    val id: Int,
    val title: String,
    @ColumnInfo(name = "favorites_mark")
    val favoritesMark: Boolean,
    val poster: ByteArray?,

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
)

data class ExpectedCardSite(
    val id: Int, // для фильтра по сайту
    val address: String, // для формирования ссылки
    @ColumnInfo(name = "use_mirror")
    val useMirror: Boolean, // для формирования ссылки
    val mirror: String?, // для формирования ссылки
)

data class ExpectedCardMovie(
    val id: Int, // для добавления/удаления в/из избранного
//    val title: String, // для отображения если нету названия у сезона
//    val poster: ByteArray?, // для отобрадения если нету постера у сезона
//    @ColumnInfo(name = "favorites_mark")
//    val favoritesMark: Boolean, // для отображения и фильтра
)

data class ExpectedCardSeason(
    val id: Int, // возможно для уникального ключа в списке, но это не точно
    val number: Int, // для отображения если нет названия
)

data class ExpectedCardEpisode(
    val id: Int, // для проставления статуса
    val number: Int, // для отображения
    val title: String?, // для отображения
    val date: LocalDateTime, // для отображения
    val link: String // для перехода в браузер
)