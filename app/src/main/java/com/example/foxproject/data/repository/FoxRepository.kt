package com.example.foxproject.data.repository

import com.example.foxproject.data.api.FoxApiService
import com.example.foxproject.data.models.Fox

class FoxRepository(
    private val apiService: FoxApiService,
) {
    private val foxesCache = mutableListOf<Fox>()
    private var currentIndex = 0

    suspend fun getFoxes(count: Int = 10): List<Fox> {
        val newFoxes = mutableListOf<Fox>()

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
                newFoxes.add(fox)
                foxesCache.add(fox)
                currentIndex++
            } catch (e: Exception) {
                // В случае ошибки пропускаем эту лису
                e.printStackTrace()
            }
        }

        return newFoxes
    }

    fun getCachedFoxes(): List<Fox> = foxesCache.toList()
}
