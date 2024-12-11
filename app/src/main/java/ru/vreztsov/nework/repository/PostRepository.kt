package ru.vreztsov.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.vreztsov.nework.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAllAsync()
    suspend fun likeById(postId: Long)
}