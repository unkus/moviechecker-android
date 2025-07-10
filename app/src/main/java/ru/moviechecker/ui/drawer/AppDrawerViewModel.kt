package ru.moviechecker.ui.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.database.sites.SitesRepository

class AppDrawerViewModel(
    private val sitesRepository: SitesRepository
) : ViewModel() {

    val sites = sitesRepository.getAllStream()
        .map { entities -> entities.map(SiteModel::fromEntity) }
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
                return AppDrawerViewModel(sitesRepository) as T
            }
        }
    }
}

data class SiteModel(
    val id: Int,
    val title: String
) {
    companion object Factory {
        fun fromEntity(entity: SiteEntity): SiteModel {
            return SiteModel(
                id = entity.id,
                title = entity.title ?: entity.address.toString()
            )
        }
    }
}