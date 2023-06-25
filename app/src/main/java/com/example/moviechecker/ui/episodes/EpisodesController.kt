package com.example.moviechecker.ui.episodes

import android.content.Context
import android.net.Uri
import com.example.moviechecker.ui.ItemController
import com.example.moviechecker.ui.favorites.FavoritesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpisodesController(private val favoritesViewModel: FavoritesViewModel) : ItemController() {

    fun onFavoriteChecked(siteAddress: Uri?, moviePageId: String?) {
        favoritesViewModel.addNewFavorite(siteAddress, moviePageId)
    }

    fun onFavoriteUnchecked(siteAddress: Uri?, moviePageId: String?) = CoroutineScope(Dispatchers.IO).launch {
        val favorite = favoritesViewModel.loadBySiteAndMovie(siteAddress, moviePageId)
        favorite?.let {
            favoritesViewModel.delete(it)
        }
    }

    override fun onOpenClicked(context: Context, link: Uri) {
        super.onOpenClicked(context, link)

    }
}