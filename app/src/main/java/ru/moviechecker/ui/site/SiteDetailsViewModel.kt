package ru.moviechecker.ui.site

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.moviechecker.SiteDetailsRoute
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.database.sites.SitesRepository

class SiteDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val sitesRepository: SitesRepository,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<SiteDetailsRoute>()

    val uiState = sitesRepository.getByIdStream(route.siteId)
        .map(SiteDetailsUiState::fromEntity)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    fun setUseMirror(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            sitesRepository.findById(route.siteId)?.let { site ->
                site.useMirror = value
                sitesRepository.updateSite(site)
            }
        }
    }

    fun setMirror(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sitesRepository.findById(route.siteId)?.let { site ->
                site.mirror = address
                sitesRepository.updateSite(site)
            }
        }
    }

    companion object {
        fun provideFactory(
            savedStateHandle: SavedStateHandle,
            sitesRepository: SitesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SiteDetailsViewModel(savedStateHandle, sitesRepository) as T
            }
        }
    }
}

data class SiteDetailsUiState(
    val id: Int,
    val mnemonic: String,
    var address: String,
    var title: String? = null,
    var poster: ByteArray? = null,
    var useMirror: Boolean = false,
    var mirror: String? = null
) {
    companion object Factory {

        fun fromEntity(entity: SiteEntity): SiteDetailsUiState {
            return SiteDetailsUiState(
                id = entity.id,
                mnemonic = entity.mnemonic,
                address = entity.address,
                title = entity.title,
                poster = entity.poster,
                useMirror = entity.useMirror,
                mirror = entity.mirror
            )
        }
    }
}