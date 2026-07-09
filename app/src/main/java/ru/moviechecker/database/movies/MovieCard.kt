package ru.moviechecker.database.movies

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.net.URI
import java.time.LocalDateTime

data class MovieCard(
    val id: Int,
    val title: String,
    @ColumnInfo(name = "favorites_mark")
    val favoritesMark: Boolean,
    val poster: ByteArray?,

    @Embedded(prefix = "site_")
    val site: MovieCardSite,
    @Embedded(prefix = "movie_")
    val movie: MovieCardMovie,
    // первый не просмотренный или последний просмотренный сезон
    @Embedded(prefix = "season_")
    val season: MovieCardSeason,
    // первый не просмотренный или последний просмотренный эпизод
    @Embedded(prefix = "episode_")
    val episode: MovieCardEpisode
)

data class MovieCardSite(
    val id: Int, // для фильтра по сайту
    val address: String, // для формирования ссылки
    @ColumnInfo(name = "use_mirror")
    val useMirror: Boolean, // для формирования ссылки
    val mirror: String?, // для формирования ссылки
)

data class MovieCardMovie(
    val id: Int, // для добавления/удаления в/из избранного
    val title: String, // для отображения если нету названия у сезона
    val poster: ByteArray?, // для отобрадения если нету постера у сезона
    @ColumnInfo(name = "favorites_mark")
    val favoritesMark: Boolean, // для отображения и фильтра
    @ColumnInfo(name = "last_season_number")
    val seasonLastNumber: Int // для отображения последний/не последний
)

data class MovieCardSeason(
    val id: Int, // возможно для уникального ключа в списке, но это не точно
    val number: Int, // для отображения если нет названия
    val title: String?, // для отображения
    val poster: ByteArray?, // для отображения
    val link: URI?, // для формирования ссылки (!!! пока не используется)
    @ColumnInfo(name = "last_episode_number")
    val lastEpisodeNumber: Int, // для отображения последний/не последний
    @ColumnInfo(name = "last_episode_date")
    val lastEpisodeDate: LocalDateTime // для сортировки
)

data class MovieCardEpisode(
    val id: Int, // для проставления статуса
    val number: Int, // для отображения
    val title: String?, // для отображения
    @ColumnInfo(name = "viewed_mark")
    val viewedMark: Boolean, // для отображения и фильтра
    val date: LocalDateTime, // для отображения
    val link: String // для перехода в браузер

)