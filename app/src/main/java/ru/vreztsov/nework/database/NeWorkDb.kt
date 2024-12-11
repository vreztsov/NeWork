package ru.vreztsov.nework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.vreztsov.nework.dao.NeWorkDao
import ru.vreztsov.nework.entity.EventEntity
import ru.vreztsov.nework.entity.JobEntity
import ru.vreztsov.nework.entity.PostEntity
import ru.vreztsov.nework.entity.UserEntity

@Database(entities = [PostEntity::class, UserEntity::class, EventEntity::class, JobEntity::class], version = 1, exportSchema = false)
abstract class NeWorkDb : RoomDatabase() {
    abstract fun neWorkDao(): NeWorkDao
}