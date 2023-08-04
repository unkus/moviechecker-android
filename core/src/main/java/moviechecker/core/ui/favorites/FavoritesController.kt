package moviechecker.core.ui.favorites

import moviechecker.core.ui.ItemController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moviechecker.core.di.database.DataService
import java.net.URI

class FavoritesController(private val dataService: DataService): ItemController() {

    fun onForgotClicked(siteAddress: URI, moviePageId: String) = CoroutineScope(Dispatchers.IO).launch {
        dataService.removeFromFavorites(siteAddress, moviePageId)
    }
}