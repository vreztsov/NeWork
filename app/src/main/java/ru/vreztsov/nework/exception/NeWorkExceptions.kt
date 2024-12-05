package ru.vreztsov.nework.exception

class RegistrationException(override val message: String): RuntimeException(message) {
    companion object {
        const val ERR_403 = "This login is already registered"
        const val ERR_415 = "Invalid photo format"
    }
}