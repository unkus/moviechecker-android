package moviechecker.core.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import moviechecker.core.di.database.favorite.FavoriteRepository
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(repository: FavoriteRepository): ViewModel() {

    val favorites = repository.favorites.asLiveData()

}