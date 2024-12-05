package ru.vreztsov.nework.auth

data class LoginInfo (
    val login: String = "",
    val password: String = "",
    val server: String? = null,
    val port: String? = null,
)

