package ru.vreztsov.nework.model

data class PostsModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)