package ru.vreztsov.nework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import ru.vreztsov.nework.R
import ru.vreztsov.nework.api.ApiService
import ru.vreztsov.nework.auth.AppAuth
import ru.vreztsov.nework.auth.AuthState
import ru.vreztsov.nework.auth.RegisterInfo
import ru.vreztsov.nework.exception.RegistrationException
import ru.vreztsov.nework.util.SingleLiveEvent
import javax.inject.Inject


@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val apiService: ApiService,
    private val appAuth: AppAuth
) : ViewModel() {

    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L

    private val _registerSuccessEvent = SingleLiveEvent<Unit>()
    val registerSuccessEvent: LiveData<Unit>
        get() = _registerSuccessEvent

    private val _registerError = MutableLiveData<String?>(null)
    val registerError: LiveData<String?>
        get() = _registerError

    private val _registerInfo =
        MutableLiveData(RegisterInfo())
    val registerInfo: LiveData<RegisterInfo>
        get() = _registerInfo


    private val _loginErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val loginErrorMessage: LiveData<Int?>
        get() = _loginErrorMessage

    private val _nameErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val nameErrorMessage: LiveData<Int?>
        get() = _nameErrorMessage

    private val _passwordErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val passwordErrorMessage: LiveData<Int?>
        get() = _passwordErrorMessage

    private val _password2ErrorMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val password2ErrorMessage: LiveData<Int?>
        get() = _password2ErrorMessage

    private var completed = false

    fun doRegister() {

        if (!completed()) {
            _registerError.value = "Register with uncompleted status error!"
            return
        }
        _registerError.value = null

        viewModelScope.launch {
            try {
                registerUser()
                if (isAuthorized)
                    _registerSuccessEvent.value = Unit
                else {
                    _registerError.value = "Unexpected register error!"
                }
            } catch (e: RegistrationException) {
                handleRegisterException(e, e.message)
            } catch (e: Exception) {
                val errText = if (e.message.toString() == "") "Unknown register error!"
                else e.message.toString()
                handleRegisterException(e, errText)
            }
        }
    }

    private fun handleRegisterException(e: Exception, errText: String) {
        Log.e("CATCH OF REGISTER USER", e.message.toString())
        _registerError.value = errText
    }

    private suspend fun registerUser() {
        lateinit var response: Response<AuthState>
        try {
            with(registerInfo.value ?: return) {
                if (avatar != null) {
                    response = apiService.registerWithPhoto(
                        login.toRequestBody("text/plain".toMediaType()),
                        password.toRequestBody("text/plain".toMediaType()),
                        username.toRequestBody("text/plain".toMediaType()),
                        MultipartBody.Part.createFormData(
                            "file", avatar.name, avatar.asRequestBody()
                        )
                    )
                } else {
                    response = apiService.registerUser(
                        login,
                        password,
                        username,
                    )
                }
            }

        } catch (e: Exception) {
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }

        if (!response.isSuccessful) {
            when (response.code()) {
                403 -> throw RegistrationException(RegistrationException.ERR_403)
                415 -> throw RegistrationException(RegistrationException.ERR_415)
                else -> {
                    val errText = if (response.message() == "")
                        "No server response" else response.message()

                    throw RuntimeException("Request declined: $errText")
                }
            }
        }
        val responseToken = response.body() ?: throw RuntimeException("body is null")
        appAuth.setAuth(
            responseToken.id,
            responseToken.token ?: throw RuntimeException("token is null")
        )
    }

    fun resetRegisterInfo(newRegisterInfo: RegisterInfo) {
        completed = resetRegisterInfoBoolean(newRegisterInfo)
    }

    private fun resetRegisterInfoBoolean(newRegisterInfo: RegisterInfo): Boolean {
        _registerError.value = null
        _registerInfo.value = newRegisterInfo
        registerInfo.value?.let {
            if (it.login.isBlank()) {
                _loginErrorMessage.value = R.string.empty_login
                return false
            }
            if (it.username.isEmpty()) {
                _nameErrorMessage.value = R.string.empty_name
                return false
            }
            if (it.password.isEmpty()) {
                _passwordErrorMessage.value = R.string.empty_password
                return false
            }
            if (it.password != it.password2) {
                _password2ErrorMessage.value = R.string.password_mismatch
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
        _nameErrorMessage.value = null
        _passwordErrorMessage.value = null
        _password2ErrorMessage.value = null
    }
}