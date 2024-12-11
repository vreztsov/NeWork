package ru.vreztsov.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.EntityListManager

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
    val likeOwnerIdList: String,
    val mentionedIdList: String,
    val link: String? = null,
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
        likeOwnerIds = EntityListManager.stringToDtoList(likeOwnerIdList),
        mentionIds = EntityListManager.stringToDtoList(mentionedIdList),
        link = link,
        attachment = attachment?.toDto(),
        coords = coords?.toDto()
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
            likeOwnerIdList = EntityListManager.dtoListToString(dto.likeOwnerIds),
            mentionedIdList = EntityListManager.dtoListToString(dto.mentionIds),
            link = dto.link,
            attachment = AttachmentEmbeddable.fromDto(dto.attachment),
            coords = CoordinatesEmbeddable.fromDto(dto.coords),
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
