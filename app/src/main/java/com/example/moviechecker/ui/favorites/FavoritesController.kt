package com.example.moviechecker.ui.favorites

import android.net.Uri
import com.example.moviechecker.ui.ItemController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesController(private val favoritesViewModel: FavoritesViewModel): ItemController() {

    fun onForgotClicked(siteAddress: Uri?, moviePageId: String?) = CoroutineScope(Dispatchers.IO).launch {
        val favorite = favoritesViewModel.loadBySiteAndMovie(siteAddress, moviePageId)
        favorite?.let {
            favoritesViewModel.delete(it)
        }
    }
}