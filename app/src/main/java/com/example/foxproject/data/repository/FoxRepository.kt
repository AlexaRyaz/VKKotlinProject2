
package com.example.foxproject.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foxproject.data.api.FoxApiService
import com.example.foxproject.data.models.Fox
import kotlinx.coroutines.flow.Flow

class FoxRepository(
    private val apiService: FoxApiService
) {
    fun getFoxesPaging(): Flow<PagingData<Fox>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,          // Количество элементов на страницу
                prefetchDistance = 2,   // Когда предзагружать следующую
                initialLoadSize = 10,   // Первоначальная загрузка
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FoxPagingSource(apiService) }
        ).flow
    }
}
