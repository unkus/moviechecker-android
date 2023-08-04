package moviechecker.data.favorite

import moviechecker.core.di.database.favorite.FavoriteRepository

class FavoriteRepositoryImpl internal constructor(dao: FavoriteDao) : FavoriteRepository {
    override val favorites = dao.favoritesDetailed
}