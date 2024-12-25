package ru.vreztsov.nework.dto

data class Job(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String? = null,
    val link: String? = null,
    val ownerId: Long = 0L
)
