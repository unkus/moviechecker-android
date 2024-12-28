package ru.moviechecker.database.movies

interface IMovie {
    val id: Int
    val siteId: Int
    val pageId: String
    var title: String
    var link: String?
    var poster: ByteArray?
    var favoritesMark: Boolean
}