package ru.vreztsov.nework.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.vreztsov.nework.api.ApiService
import ru.vreztsov.nework.dao.NeWorkDao
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.entity.PostEntity
import ru.vreztsov.nework.entity.toDto
import ru.vreztsov.nework.entity.toEntity
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
   private val dao: NeWorkDao,
    private val apiService: ApiService,
) : PostRepository {
    override val data: Flow<List<Post>> = dao.getAll()
        .map (List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAllAsync() {
        val response = apiService.getAll()
        if (!response.isSuccessful) {
            return // TODO везде, где return, должна быть throw error
        }
        val body = response.body() ?: return
        dao.insert(body.toEntity())

    }
}