package ru.vreztsov.nework.model

import android.net.Uri
import ru.vreztsov.nework.dto.AttachmentType
import java.io.File

data class MediaModel(
    val uri: Uri? = null,
    val file: File? = null,
    val attachmentType: AttachmentType? = null
)