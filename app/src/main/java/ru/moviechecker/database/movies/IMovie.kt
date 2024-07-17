package ru.moviechecker.database.movies

import java.net.URI

interface IMovie {
    val id: Int
    val siteId: Int
    val pageId: String
    var title: String
    var link: URI?
    var poster: ByteArray?
    var favoritesMark: Boolean
}