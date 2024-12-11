package ru.vreztsov.nework.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.vreztsov.nework.dao.NeWorkDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {
    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): NeWorkDb = Room.databaseBuilder(context, NeWorkDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideNeWorkDao(
        NeWorkDb: NeWorkDb
    ): NeWorkDao = NeWorkDb.neWorkDao()
}