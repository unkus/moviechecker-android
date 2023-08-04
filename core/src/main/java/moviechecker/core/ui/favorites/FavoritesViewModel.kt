package moviechecker.core.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import moviechecker.core.CheckerApplication
import moviechecker.core.di.database.favorite.FavoriteRepository

class FavoritesViewModel(repository: FavoriteRepository): ViewModel() {

    val favorites = repository.favorites.asLiveData()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dataService =
                    (this[APPLICATION_KEY] as CheckerApplication).dataService
                FavoritesViewModel(dataService.favoriteRepository)
            }
        }
    }
}