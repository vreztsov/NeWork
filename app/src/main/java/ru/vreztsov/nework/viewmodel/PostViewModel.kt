package ru.vreztsov.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.vreztsov.nework.auth.AppAuth
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.model.PostsModelState
import ru.vreztsov.nework.repository.PostRepository
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0,
    published = "",
    authorPosition = ""
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val cached = repository.data
        ?.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Post>>? = null
//        appAuth
//        .authStateFlow
//        .flatMapLatest { (myId, _) ->
//            cached.map { pagingData ->
//                pagingData.map { post ->
//                    post.copy(ownedByMe = post.authorId == myId)
//                }
//            }
//        }

    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<PostsModelState>()
    val dataState: LiveData<PostsModelState>
        get() = _dataState

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = PostsModelState(loading = true)
//            repository.getInitialPostPage()
            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = PostsModelState(refreshing = true)
//            repository.getInitialPostPage()
            _dataState.value = PostsModelState()
        } catch (e: Exception) {
            _dataState.value = PostsModelState(error = true)
        }
    }

    fun edit(post: Post){
        edited.value = post
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
//            repository.likeById(id)
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