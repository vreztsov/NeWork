package ru.vreztsov.nework.entity

import ru.vreztsov.nework.dto.Attachment
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Coordinates

data class AttachmentEmbeddable(
    val url: String,
    val type: AttachmentType,
    ){
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

data class CoordinatesEmbeddable(
    val latitude : String?,
    val longitude : String?,
) {
    fun toDto() =
        Coordinates(latitude, longitude)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordinatesEmbeddable(it.lat, it.long)
        }
    }
}


