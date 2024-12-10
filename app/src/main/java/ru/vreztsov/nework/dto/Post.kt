package ru.vreztsov.nework.dto

data class Post(
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String = "",
    val authorPosition: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likeOwnerIdList: List<Long> = emptyList(),
    val mentionedMe: Boolean = false,
    val mentionedIdList: List<Long> = emptyList(),
    val link: String? = null,
    //val attachment: Attachment? = null, TODO допилить попозже
    val coordinates: Coordinates? = null,
    val ownedByMe: Boolean = false,
)
