package ru.vreztsov.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long
)
