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
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val users: Map<String, UserPreview> = hashMapOf()
) {
    var ownedByMe: Boolean = false
}
