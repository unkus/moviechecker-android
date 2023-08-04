package moviechecker.core.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import moviechecker.core.di.database.favorite.FavoriteRepository

class FavoritesViewModel(repository: FavoriteRepository): ViewModel() {

    val favorites = repository.favorites.asLiveData()

}