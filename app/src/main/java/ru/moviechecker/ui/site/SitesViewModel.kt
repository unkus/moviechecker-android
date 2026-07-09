package ru.moviechecker.ui.site

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.database.sites.SitesRepository

class SitesViewModel(
    sitesRepository: SitesRepository
) : ViewModel() {

    val sites = sitesRepository.getAllStream()
        .map { it.map(SiteModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    companion object {
        fun provideFactory(
            sitesRepository: SitesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SitesViewModel(sitesRepository) as T
            }
        }
    }
}

data class SiteModel(
    val id: Int,
    val mnemonic: String,
    val poster: ByteArray? = null,
    var title: String,
    var address: String,
    var useMirror: Boolean = false,
    var mirror: String? = null
) {
    companion object Factory {

        fun fromEntity(entity: SiteEntity): SiteModel {
            return SiteModel(
                id = entity.id,
                mnemonic = entity.mnemonic,
                poster = entity.poster,
                title = entity.title ?: entity.mnemonic,
                address = entity.address,
                useMirror = entity.useMirror,
                mirror = entity.mirror
            )
        }
    }
}