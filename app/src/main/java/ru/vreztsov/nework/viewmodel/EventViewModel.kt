package ru.vreztsov.nework.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.vreztsov.nework.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val appAuth: AppAuth
): ViewModel() {
    val isAuthorized: Boolean
        get() = appAuth.authStateFlow.value.id != 0L
}