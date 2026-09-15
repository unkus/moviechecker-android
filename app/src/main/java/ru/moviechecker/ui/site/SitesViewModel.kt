package ru.moviechecker.ui.site

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.moviechecker.checkerApplication
import ru.moviechecker.database.site.SiteEntity
import ru.moviechecker.database.site.SiteRepository

class SitesViewModel(
    siteRepository: SiteRepository
) : ViewModel() {

    val sites = siteRepository.getAllStream()
        .map { it.map(SiteModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    companion object {
        val Factory = viewModelFactory {
            initializer {
                SitesViewModel(
                    checkerApplication().container.siteRepository
                )
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