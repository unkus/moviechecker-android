package com.example.moviechecker.ui.favorites

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moviechecker.CheckerApplication
import com.example.moviechecker.model.favorite.Favorite
import com.example.moviechecker.model.favorite.FavoriteDao
import com.example.moviechecker.model.favorite.FavoriteDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(private val dao: FavoriteDao): ViewModel() {

    val favorites: LiveData<List<Favorite>> = dao.favorites.asLiveData()
    val favoritesDetailed: LiveData<List<FavoriteDetail>> = dao.favoritesDetailed.asLiveData()

    suspend fun findByMovie(movie_id: Int): Favorite? {
        return dao.findByMovie(movie_id)
    }

    suspend fun loadBySiteAndMovie(siteAddress: Uri?, moviePageId: String?): Favorite? {
        return dao.loadBySiteAndMovie(siteAddress, moviePageId)
    }

    fun addNewFavorite(siteAddress: Uri?, moviePageId: String?) = viewModelScope.launch(Dispatchers.IO) {
        Log.i("TEST", "siteAddress: $siteAddress, moviePageId: $moviePageId")
        val movie = dao.loadMovieBySiteAndPage(siteAddress, moviePageId)
        movie.let {
            val favorite = Favorite(movie.id)
            insert(favorite)
        }
    }

    fun insert(favorite: Favorite) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(favorite)
    }

    fun update(favorite: Favorite) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(favorite)
    }

    fun delete(favorite: Favorite) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(favorite)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val database = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CheckerApplication).database
                FavoritesViewModel(database.favoriteDao())
            }
        }
    }
}