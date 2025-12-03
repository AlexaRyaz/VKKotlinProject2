package com.example.foxproject.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foxproject.data.models.Fox
import com.example.foxproject.data.repository.FoxRepository
import com.example.foxproject.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoxUiState(
    val foxes: List<Fox> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class FoxViewModel : ViewModel() {
    private val repository = FoxRepository(NetworkModule.foxApiService)

    private val _uiState = MutableStateFlow(FoxUiState(isLoading = true))
    val uiState: StateFlow<FoxUiState> = _uiState.asStateFlow()


    init {
        loadFoxes()
    }

    fun loadFoxes(count: Int = 1) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                val newFoxes = repository.getFoxes(count)

                _uiState.update { currentState ->
                    currentState.copy(
                        foxes = currentState.foxes + newFoxes,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to load foxes: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadMore() {
        loadFoxes(1) 
    }

    fun retry() {
        if (_uiState.value.foxes.isEmpty()) {
            loadFoxes()
        } else {
            loadMore()
        }
    }
}
