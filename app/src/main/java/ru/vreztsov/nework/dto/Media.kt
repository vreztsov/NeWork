package ru.vreztsov.nework.dto

import java.io.File


data class MediaUpload(val file: File)
data class MediaResponse(val url: String)
