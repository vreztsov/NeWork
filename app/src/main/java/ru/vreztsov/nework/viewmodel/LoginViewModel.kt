package ru.vreztsov.nework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.vreztsov.nework.R
import ru.vreztsov.nework.api.ApiService
import ru.vreztsov.nework.auth.AppAuth
import ru.vreztsov.nework.auth.AuthState
import ru.vreztsov.nework.auth.LoginInfo
import ru.vreztsov.nework.exception.LoginException
import ru.vreztsov.nework.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth
) : ViewModel() {
    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    val profileId: Long
        get() = appAuth.authStateFlow.value.id

    private val _loginSuccessEvent = SingleLiveEvent<Unit>()
    val loginSuccessEvent: LiveData<Unit>
        get() = _loginSuccessEvent

    private val _loginError = MutableLiveData<String?>(null)
    val loginError: LiveData<String?>
        get() = _loginError

    private val _loginInfo = MutableLiveData(LoginInfo())
    val loginInfo: LiveData<LoginInfo>
        get() = _loginInfo

    private val _loginErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val loginErrorMessage: LiveData<Int?>
        get() = _loginErrorMessage

    private val _passwordErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val passwordErrorMessage: LiveData<Int?>
        get() = _passwordErrorMessage

    private var completed = false

    fun doLogin() {

        if (!completed()) {
            _loginError.value = "Login with uncompleted status error!"
            return
        }

        _loginError.value = null

        viewModelScope.launch {
            try {
                updateUser()
                if (isAuthorized)
                    _loginSuccessEvent.value = Unit
                else {
                    _loginError.value = "Unexpected login error!"
                }
            } catch (e: LoginException) {
                handleLoginException(e, e.message)
            } catch (e: Exception) {
                val errText = if (e.message.toString() == "") "Unknown login error!"
                else e.message.toString()
                handleLoginException(e, errText)
            }
        }

    }

    private fun handleLoginException(e: Exception, errText: String) {
        Log.e("CATCH OF UPDATE USER", e.message.toString())
        _loginError.value = errText
    }

    private suspend fun updateUser() {
        lateinit var response: Response<AuthState>
        try {
            with(loginInfo.value ?: return) {
                response = apiService.updateUser(
                    login,
                    password
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }
        if (!response.isSuccessful) {
            when (response.code()) {
                400 -> throw LoginException(LoginException.ERR_400)
                404 -> throw LoginException(LoginException.ERR_404)
                else -> {
                    val errText = if (response.message() == "")
                        "No server response" else response.message()
                    throw RuntimeException("Request declined: $errText")
                }
            }
        }
        val authState = response.body() ?: throw RuntimeException("body is null")
        appAuth.setAuth(authState.id, authState.token ?: throw RuntimeException("token is null"))
    }

    fun resetLoginInfo(newLoginInfo: LoginInfo) {
        completed = resetLoginInfoBoolean(newLoginInfo)
    }

    private fun resetLoginInfoBoolean(newLoginInfo: LoginInfo): Boolean {
        _loginError.value = null
        _loginInfo.value = newLoginInfo

        loginInfo.value?.let {
            if (it.login.isBlank()) {
                _loginErrorMessage.value = R.string.empty_login
                return false
            }
            if (it.password.isEmpty()) {
                _passwordErrorMessage.value = R.string.empty_password
                return false
            }
        }
//        viewModelScope.launch { delay(300) }
        return true
    }

    fun completed(): Boolean = completed

    fun resetErrors() {
        completed = false
        _loginErrorMessage.value = null
        _passwordErrorMessage.value = null
    }

    fun doLogout(){
        appAuth.removeAuth()
    }
}