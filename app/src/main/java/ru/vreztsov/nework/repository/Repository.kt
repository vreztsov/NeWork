package ru.vreztsov.nework.repository

import kotlinx.coroutines.flow.Flow
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Job
import ru.vreztsov.nework.dto.MediaResponse
import ru.vreztsov.nework.dto.MediaUpload
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.dto.User

interface Repository {
    val data: Flow<List<Post>>
    val dataUsers: Flow<List<User>>
    val dataJobs: Flow<List<Job>>
//    val myJobs: LiveData<List<Job>>
    suspend fun getAllAsync()
    suspend fun getAllUsersAsync()
    suspend fun getJobsAsync(ownerId: Long)
    suspend fun getMyJobsAsync(ownerId: Long)
    suspend fun insertMyJobs(list: List<Job>, myId: Long)
    suspend fun likeById(postId: Long)
    suspend fun onWallLikeById(authorId: Long, postId: Long)
    suspend fun removeById(id: Long)
    suspend fun getUserById(id: Long): User?
    suspend fun savePost(post: Post)
    suspend fun saveMyJob(job: Job)
    suspend fun removeMyJobById(id: Long)
    suspend fun savePostWithAttachment(post: Post, upload: MediaUpload, attachmentType: AttachmentType)
    suspend fun upload(upload: MediaUpload): MediaResponse
    suspend fun loadUserWall(id: Long)
    suspend fun loadMyWall()

}