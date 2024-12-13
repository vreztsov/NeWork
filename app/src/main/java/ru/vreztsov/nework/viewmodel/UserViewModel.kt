package ru.vreztsov.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
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
        get() = flow {
//            while (true) {
                getData()
                emit(_dataUsersList)
//                delay(10_000)
//            }
        }

    private var _dataUsersList: List<User> = listOf()

    init {
        getData()
    }

    private fun getData() = viewModelScope.launch {
        try {
            repository.getAllUsersAsync()
        } catch (e: Exception) {
            Log.e("UsersViewModel", "Failed to get users list")
        }
        repository.dataUsers.collectLatest {
            _dataUsersList = it
        }
    }

    fun getUserById(id: Long): User? = _dataUsersList.find { it.id == id }
}