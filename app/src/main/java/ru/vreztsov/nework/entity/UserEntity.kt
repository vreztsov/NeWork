package ru.vreztsov.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.vreztsov.nework.dto.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null
) {
    fun toDto() = User(
        id = id,
        login = login,
        name = name,
        avatar = avatar
    )

    companion object {
        fun fromDto(dto: User) = UserEntity(
            id = dto.id,
            login = dto.login,
            name = dto.name,
            avatar = dto.avatar
        )
    }
}

fun List<UserEntity>.toDto(): List<User> = map(UserEntity::toDto)
fun List<User>.toEntity(): List<UserEntity> = map(UserEntity::fromDto)
