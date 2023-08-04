package moviechecker.core.di.database.favorite

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    val favorites: Flow<List<Favorite>>
}