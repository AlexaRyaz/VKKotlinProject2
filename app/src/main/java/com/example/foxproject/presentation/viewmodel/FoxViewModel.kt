package com.example.foxproject.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foxproject.data.models.Fox
import com.example.foxproject.data.repository.FoxRepository
import com.example.foxproject.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import androidx.paging.cachedIn
import androidx.paging.PagingData

class FoxViewModel : ViewModel() {
    private val repository = FoxRepository(NetworkModule.foxApiService)
    val foxesPagingFlow: Flow<PagingData<Fox>> = repository
        .getFoxesPaging()
        .cachedIn(viewModelScope)
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    fun clearError() {
        _errorState.value = null
    }
}