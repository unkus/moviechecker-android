package ru.moviechecker.ui.site

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.moviechecker.checkerApplication
import ru.moviechecker.database.site.SiteEntity
import ru.moviechecker.database.site.SiteRepository
import ru.moviechecker.model.Identifiable

class SiteDetailsViewModel(private val siteRepository: SiteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SiteDetailsUiState())
    val uiState: StateFlow<SiteDetailsUiState> = _uiState.asStateFlow()

    fun loadData(id: Int) {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true) }
            try {
                val entity = siteRepository.findById(id)
                if (entity != null) {
                    _uiState.update {
                        it.copy(
                            data = SiteData(
                                id = entity.id,
                                poster = entity.poster
                            ),
                            form = SiteFormState(
                                mnemonic = entity.mnemonic,
                                title = entity.title ?: "",
                                address = entity.address,
                                useMirror = entity.useMirror,
                                mirror = entity.mirror ?: ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(this.javaClass.simpleName, "Ошибка загрузки", e)
            } finally {
                _uiState.update { state -> state.copy(isLoading = false) }
            }
        }
    }

    fun updateForm(form: SiteFormState) {
        _uiState.update { it.copy(form = form) }
    }

    // Сохранить в БД
    fun save() {
        val state = _uiState.value
        if (!state.form.isValid) return

        viewModelScope.launch {
            _uiState.update { state -> state.copy(isSaving = true) }
            try {
                val entity = SiteEntity(
                    id = state.data.id,
                    mnemonic = state.form.mnemonic,
                    address = state.form.address,
                    title = state.form.title.ifBlank { null },
                    poster = state.data.poster,
                    useMirror = state.form.useMirror,
                    mirror = state.form.mirror.ifBlank { null }
                )
                if (entity.id == 0) {
                    siteRepository.create(entity)
                } else {
                    siteRepository.update(entity)
                }
            } finally {
                _uiState.update { state -> state.copy(isSaving = false) }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                SiteDetailsViewModel(
                    checkerApplication().container.siteRepository
                )
            }
        }
    }
}

data class SiteDetailsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val data: SiteData = SiteData(),
    val form: SiteFormState = SiteFormState()
)

data class SiteFormState(
    val mnemonic: String = "",
    val title: String = "",
    val address: String = "",
    val useMirror: Boolean = false,
    val mirror: String = ""
) {
    val isValid: Boolean
        get() = mnemonic.isNotBlank()
                && address.isNotBlank()
                && (!useMirror || mirror.isNotBlank())
}

data class SiteData(
    override val id: Int = 0,
    val poster: ByteArray? = null
) : Identifiable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SiteData

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}