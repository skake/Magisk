package com.topjohnwu.magisk.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.topjohnwu.magisk.model.entity.MagiskModule

@Database(
    version = 6,
    entities = [MagiskModule.Repository::class]
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val NAME = "database"
    }

    abstract fun repoDao(): RepositoryDao

}
