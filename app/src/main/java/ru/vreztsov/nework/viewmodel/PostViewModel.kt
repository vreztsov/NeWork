package ru.vreztsov.nework.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.vreztsov.nework.auth.AppAuth
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Coordinates
import ru.vreztsov.nework.dto.MediaUpload
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.dto.UserPreview
import ru.vreztsov.nework.model.MediaModel
import ru.vreztsov.nework.model.PostsModelState
import ru.vreztsov.nework.repository.Repository
import ru.vreztsov.nework.util.AndroidUtils
import ru.vreztsov.nework.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0,
    published = "1",
)

private val emptyMedia = MediaModel(null, null, null)

@HiltViewModel
class PostViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
    private val appAuth: AppAuth
) : AndroidViewModel(application) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<List<Post>>
        get() = appAuth
            .authStateFlow
            .flatMapLatest { (myId, _) ->
                repository.data
                    .map { posts ->
                        posts.map { post ->
                            post.copy(
                                likedByMe = post.likeOwnerIds.contains(myId),
                                mentionedMe = post.mentionIds.contains(myId),
                            ).apply { ownedByMe = post.authorId == myId }
                        }
                    }
            }


    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<PostsModelState>()
    val dataState: LiveData<PostsModelState>
        get() = _dataState

    private val _media = MutableLiveData(
        MediaModel(
            edited.value?.attachment?.url?.toUri(),
            edited.value?.attachment?.url?.toUri()?.toFile(),
            edited.value?.attachment?.type
        )
    )
    val media: LiveData<MediaModel>
        get() = _media

    private val _postCreated = SingleLiveEvent<PostsModelState>()
    val postCreated: LiveData<PostsModelState>
        get() = _postCreated

    private var mentionedIdList: List<Long> =
        edited.value?.mentionIds ?: mutableListOf()

    private var userMap: MutableMap<String, UserPreview> =
        edited.value?.users?.toMutableMap() ?: hashMapOf()

    private var location: Coordinates? = edited.value?.coords

    init {
        loadData()
    }

    fun loadData() = viewModelScope.launch {
        try {
            _dataState.value = PostsModelState(loading = true)
            repository.getAllAsync()
            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = PostsModelState(refreshing = true)
            repository.getAllAsync()
            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

    fun edit(post: Post) {
        edited.value = post
        // при случае поставь в модель аттачмент редактируемого поста.
    // Осторожнее с файлом в _media - он может быть только локальным.
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            if (!isAuthorized) throw RuntimeException("User is not authorized")
            repository.likeById(id)
//            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

    fun onWallLikeById(authorId: Long, postId: Long) = viewModelScope.launch {
        try {
            if (!isAuthorized) throw RuntimeException("User is not authorized")
            repository.onWallLikeById(authorId, postId)
        } catch (e: Exception) {
            Log.e("PostViewModel", "Failed to like post with id = $postId on wall")
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

    fun changeContent(
        content: String,
        link: String?
    ) {
        val text = content.trim()
        if (edited.value?.content == text &&
            edited.value?.link == link &&
            (edited.value?.mentionIds ?: emptyList()) == mentionedIdList &&
            edited.value?.coords == location &&
            AndroidUtils.equalsAttachment(
                edited.value?.attachment,
                _media.value?.uri?.toString(),
                _media.value?.attachmentType
            )
        ) return
        edited.value = edited.value?.copy(
            content = text,
            link = link,
            mentionIds = mentionedIdList,
            coords = location,
            attachment = AndroidUtils.getAttachment(
                _media.value?.uri?.toString(),
                _media.value?.attachmentType
            )
        )
    }

    fun save() {
        edited.value?.let { savingPost ->
            _postCreated.value = PostsModelState()
            viewModelScope.launch {
                try {
                    when (_media.value) {
                        emptyMedia -> {
                            repository.savePost(savingPost)
                        }

                        else -> media.value?.let { model ->
                            if (model.file != null && model.attachmentType != null) {
                                repository.savePostWithAttachment(
                                    savingPost,
                                    MediaUpload(model.file),
                                    model.attachmentType
                                )
                            }
                        }
                    }
                    _dataState.value = PostsModelState()
                } catch (e: Exception) {
                    _dataState.value = PostsModelState(error = true)
                }
            }
        }
        edited.value = empty
        _media.value = emptyMedia
        mentionedIdList = emptyList()
        location = null
//        _photo.value = noPhoto // опустошить поля для вложений
    }

    fun changeMedia(uri: Uri?, file: File?, type: AttachmentType?) {
        _media.value = MediaModel(uri, file, type)
    }

    fun setCoords(latitude: Double?, longitude: Double?) {
        location = Coordinates(latitude.toString(), longitude.toString())
    }

    fun withCoords(receiver: (latitude: Double?, longitude: Double?) -> Unit) {
        receiver(location?.lat?.toDoubleOrNull(), location?.long?.toDoubleOrNull())
    }

    fun changeMentioned(list: List<User>) {
        val result = mentionedIdList
            .filter {
                if (list.any { user -> user.id == it }) {
                    true
                } else {
                    userMap.remove(it.toString())
                    false
                }
            }

        mentionedIdList = result.plus(list
            .filterNot { mentionedIdList.contains(it.id) }
            .onEach {
                userMap[it.id.toString()] = UserPreview(it.name, it.avatar)
            }.map { it.id }
        )

    }

    fun loadUserWall(id: Long) = viewModelScope.launch {
        try {
            repository.loadUserWall(id)
        } catch (e: Exception){
            Log.e("PostViewModel", "Failed to load wall of user with id = $id")
        }
    }

    fun loadMyWall() = viewModelScope.launch {
        try {
            repository.loadMyWall()
        } catch (e: Exception){
            Log.e("PostViewModel", "Failed to load my wall")
        }
    }



}