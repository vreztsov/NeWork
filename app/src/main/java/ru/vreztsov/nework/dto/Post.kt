package ru.vreztsov.nework.dto

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val mentionIds: List<Long>? = null,
    val mentionedMe: Boolean = false,
    val likeOwnerIds: List<Long>? = null,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)
