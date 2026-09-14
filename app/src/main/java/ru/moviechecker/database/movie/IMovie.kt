package ru.moviechecker.database.movie

interface IMovie {
    val id: Int
    val siteId: Int
    val pageId: String
    var title: String
    var link: String?
    var poster: ByteArray?
    var favoritesMark: Boolean
    var kinopoiskId: String?
}