package ru.vreztsov.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.EntityCollectionManager

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    val likes: Int,
    val likedByMe: Boolean,
    val likeOwnerIdList: String,
    val mentionedIdList: String,
    val link: String? = null,
    val users: String,
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    @Embedded
    val coords: CoordinatesEmbeddable? = null,
) {
    fun toDto() = Post(
        id = id,
        author = author,
        authorId = authorId,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likeOwnerIds = EntityCollectionManager.stringToDtoList(likeOwnerIdList),
        mentionIds = EntityCollectionManager.stringToDtoList(mentionedIdList),
        link = link,
        attachment = attachment?.toDto(),
        coords = coords?.toDto(),
        users = EntityCollectionManager.stringToDtoUserPreviewMap(users)
    )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            id = dto.id,
            author = dto.author,
            authorId = dto.authorId,
            authorAvatar = dto.authorAvatar,
            authorJob = dto.authorJob,
            content = dto.content,
            published = dto.published,
            likes = dto.likeOwnerIds.size,
            likedByMe = dto.likedByMe,
            likeOwnerIdList = EntityCollectionManager.dtoListToString(dto.likeOwnerIds),
            mentionedIdList = EntityCollectionManager.dtoListToString(dto.mentionIds),
            link = dto.link,
            attachment = AttachmentEmbeddable.fromDto(dto.attachment),
            coords = CoordinatesEmbeddable.fromDto(dto.coords),
            users = EntityCollectionManager.dtoMapToString(dto.users)
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
