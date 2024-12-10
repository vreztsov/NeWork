package ru.vreztsov.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.vreztsov.nework.dto.Post
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor() : PostRepository {
    override val data: Flow<PagingData<Post>>? = null

}