package com.example.foxproject.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.foxproject.data.api.FoxApiService
import com.example.foxproject.data.models.Fox

class FoxPagingSource(
    private val apiService: FoxApiService
) : PagingSource<Int, Fox>() {

    private var currentIndex = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Fox> {
        return try {
            // Начальная страница
            val page = params.key ?: 0

            // Загружаем по 5 лис за раз
            val count = params.loadSize.coerceAtMost(5)
            val foxes = mutableListOf<Fox>()

            repeat(count) {
                try {
                    val response = apiService.getRandomFox()
                    val fox = Fox(
                        id = currentIndex,
                        imageUrl = response.image,
                        sourceUrl = response.link,
                        index = currentIndex + 1,
                        imageWidth = (300..600).random(),
                        imageHeight = (300..800).random()
                    )
                    foxes.add(fox)
                    currentIndex++
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Следующая страница
            val nextKey = if (foxes.size < count) null else page + 1

            LoadResult.Page(
                data = foxes,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Fox>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}