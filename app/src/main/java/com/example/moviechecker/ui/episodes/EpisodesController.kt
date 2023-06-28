package com.example.moviechecker.ui.episodes

import android.content.Context
import com.example.moviechecker.model.CheckerRoomDatabase
import com.example.moviechecker.model.State
import com.example.moviechecker.model.episode.EpisodeDetail
import com.example.moviechecker.model.favorite.Favorite
import com.example.moviechecker.ui.ItemController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class EpisodesController(database: CheckerRoomDatabase) : ItemController() {

    private val favoriteDao = database.favoriteDao()
    private val movieDao = database.movieDao()
    private val episodeDao = database.episodeDao()

    fun onFavoriteChecked(episode: EpisodeDetail) {
        runBlocking(Dispatchers.IO) {
            val movie =
                movieDao.loadMovieBySiteAddressAndPageId(episode.siteAddress, episode.moviePageId)
            movie?.let {
                favoriteDao.insert(Favorite(it.id))
            }
        }
    }

    fun onFavoriteUnchecked(episode: EpisodeDetail) = CoroutineScope(Dispatchers.IO).launch {
        val favorite = favoriteDao.loadBySiteAndMovie(episode.siteAddress, episode.moviePageId)
        favorite?.let {
            favoriteDao.delete(it)
        }
    }

    fun setViewed(episodeDetail: EpisodeDetail) {
        runBlocking(Dispatchers.IO) {
            val episode = episodeDao.findBySiteAndMovieAndSeasonAndNumber(episodeDetail.siteAddress, episodeDetail.moviePageId, episodeDetail.seasonNumber, episodeDetail.number)
            episode?.let {
                it.state = State.VIEWED
                episodeDao.update(it)
            }
        }
    }
}