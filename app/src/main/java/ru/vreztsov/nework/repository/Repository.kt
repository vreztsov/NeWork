package ru.vreztsov.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.dto.User

interface Repository {
    val data: Flow<List<Post>>
    val dataUsers: Flow<List<User>>
    suspend fun getAllAsync()
    suspend fun getAllUsersAsync()
    suspend fun likeById(postId: Long)
    suspend fun getUserById(id: Long): User?
}