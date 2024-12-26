package ru.vreztsov.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vreztsov.nework.auth.AppAuth
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.repository.Repository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val appAuth: AppAuth,
    private val repository: Repository,
) : AndroidViewModel(application) {
    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    val dataUsersList
        get() = repository.dataUsers
//        get() = flow {
////            while (true) {
//            getData()
//            emit(_dataUsersList)
////                delay(10_000)
////            }
//        }

    private var _dataUsersList: List<User> = mutableListOf()

    val selectedUsersList: LiveData<List<User>>
        get() = _selectedUsersList

    private var _selectedUsersList = MutableLiveData<List<User>>(emptyList())

    init {
        getData()
    }

    private fun getData() = viewModelScope.launch {
        try {
            Log.i("Repository", "Loading users list...")
            repository.getAllUsersAsync()
            Log.i("Repository", "Users list ready")
        } catch (e: Exception) {
            Log.e("UserViewModel", "Failed to get users list")
        }
        repository.dataUsers.collectLatest {
            _dataUsersList = it
        }
    }

    fun getUserById(id: Long): User? = _dataUsersList.find { it.id == id }

//    fun saveSelected(list: List<User>) {
//        _selectedUsersList.value = list
//    }


    fun saveSelected(list: List<Long>) {
        val userList = list.mapNotNull { id -> _dataUsersList.find { it.id == id } }
        _selectedUsersList.value = userList
    }
}