package ru.vreztsov.nework.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.vreztsov.nework.api.ApiService
import ru.vreztsov.nework.dao.NeWorkDao
import ru.vreztsov.nework.dto.Attachment
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Job
import ru.vreztsov.nework.dto.MediaResponse
import ru.vreztsov.nework.dto.MediaUpload
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.entity.JobEntity
import ru.vreztsov.nework.entity.PostEntity
import ru.vreztsov.nework.entity.UserEntity
import ru.vreztsov.nework.entity.toDto
import ru.vreztsov.nework.entity.toEntity
import ru.vreztsov.nework.error.*
import ru.vreztsov.nework.util.SuspendReceiver
import ru.vreztsov.nework.util.SuspendSupplier
import java.io.IOException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val dao: NeWorkDao,
    private val apiService: ApiService,
) : Repository {
    override val data: Flow<List<Post>> = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override val dataUsers: Flow<List<User>> = dao.getUsers()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)
    override val dataJobs: Flow<List<Job>> = dao.getJobs()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)


    override suspend fun getAllAsync() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw UnknownError
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getAllUsersAsync() {
        try {
            val response = apiService.getAllUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw UnknownError
            dao.insertUsers(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getJobsAsync(ownerId: Long) {
        try {
            val response = apiService.getUserJobs(ownerId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw UnknownError
            dao.insertJobs(body.toEntity(ownerId))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getMyJobsAsync(ownerId: Long) {
        try {
            val response = apiService.getMyJobs()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw UnknownError
            dao.insertJobs(body.toEntity(ownerId))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun insertMyJobs(list: List<Job>, myId: Long){
        dao.insertJobs(list.toEntity(myId))
    }

    private suspend fun <DTO> getAsync(
        responseGetter: SuspendSupplier<Response<List<DTO>>>,
        daoInserter: SuspendReceiver<List<DTO>>
    ) {
        try {
            val response: Response<List<DTO>> = responseGetter.invoke()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw UnknownError
            daoInserter.invoke(body)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(postId: Long) {
//        dao.likeById(postId)
        likeByIdOnServer(postId = postId)
    }

    override suspend fun onWallLikeById(authorId: Long, postId: Long) {
        likeByIdOnServer(authorId, postId)
    }

    private suspend fun likeByIdOnServer(authorId: Long? = null, postId: Long) {
        try {
            val getThePostResponse = authorId?.let {
                apiService.getFromWallById(it, postId)
            } ?: apiService.getById(postId)
            if (!getThePostResponse.isSuccessful) {
                throw ApiError(getThePostResponse.code(), getThePostResponse.message())
            }
            val body = getThePostResponse.body() ?: throw ApiError(
                getThePostResponse.code(),
                getThePostResponse.message()
            )
            val likeResponse =
                if (body.likedByMe) {
                    authorId?.let {
                        apiService.onWallDislikeById(it, postId)
                    } ?: apiService.dislikeById(postId)
                }
                else {
                    authorId?.let {
                        apiService.onWallLikeById(it, postId)
                    } ?: apiService.likeById(postId)
                }
            if (!likeResponse.isSuccessful) {
                throw ApiError(likeResponse.code(), likeResponse.message())
            }
            val likeBody = likeResponse.body() ?: throw ApiError(
                likeResponse.code(),
                likeResponse.message()
            )
            dao.insert(PostEntity.fromDto(likeBody))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getUserById(id: Long): User? {
        return dao.getUserById(id)?.toDto()
        // TODO хз, оно вообще надо
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): MediaResponse {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun savePost(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                Log.e(response.code().toString(), response.message())
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveMyJob(job: Job) {
        try {
            val response = apiService.saveMyJob(job)
            if (!response.isSuccessful) {
                Log.e(response.code().toString(), response.message())
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insertJob(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeMyJobById(id: Long) {
        try {
            val response = apiService.removeMyJobById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeByIdJob(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun savePostWithAttachment(
        post: Post,
        upload: MediaUpload,
        attachmentType: AttachmentType
    ) {
        try {
            val media = upload(upload)
            val postWithAttachment = post.copy(
                attachment =
                Attachment(
                    url = media.url,
                    type = attachmentType,
                )
            )
            savePost(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun loadUserWall(id: Long) {
        try {
            val response = apiService.loadUserWall(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun loadMyWall() {
        try {
            val response = apiService.loadMyWall()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}