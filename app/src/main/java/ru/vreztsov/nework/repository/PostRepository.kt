package ru.vreztsov.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.vreztsov.nework.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>?
}