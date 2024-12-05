package ru.vreztsov.nework.auth

import java.io.File

data class RegisterInfo(
    val username: String = "",
    val login: String = "",
    val password: String = "",
    val password2: String = "",
    val server: String? = null,
    val port: String? = null,
    val avatar: File? = null,
)
