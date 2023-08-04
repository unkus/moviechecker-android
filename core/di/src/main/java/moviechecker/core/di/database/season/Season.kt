package moviechecker.core.di.database.season

import moviechecker.core.di.Linkable

interface Season : Linkable {
    val movieId: Int
    val number: Int
}