package ru.vreztsov.nework.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.vreztsov.nework.auth.AppAuth
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.model.PostsModelState
import ru.vreztsov.nework.repository.Repository
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0,
    published = "",
    authorJob = ""
)

@HiltViewModel
class PostViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
    private val appAuth: AppAuth
) : AndroidViewModel(application) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<List<Post>>
        get() = appAuth
            .authStateFlow
            .flatMapLatest { (myId, _) ->
                repository.data
                    .map { posts ->
                        posts.map { post ->
                            post.copy(
                                likedByMe = post.likeOwnerIds?.contains(myId) ?: false,
                                mentionedMe = post.mentionIds?.contains(myId) ?: false,
                                ownedByMe = post.authorId == myId
                            )
                        }
                    }
            }.asLiveData(Dispatchers.Default)

    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<PostsModelState>()
    val dataState: LiveData<PostsModelState>
        get() = _dataState

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
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            if (!isAuthorized) throw RuntimeException("User is not authorized")
            repository.likeById(id)
            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
//            repository.removeById(id)
            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

}