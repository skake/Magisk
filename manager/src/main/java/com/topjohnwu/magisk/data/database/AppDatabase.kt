package com.topjohnwu.magisk.data.database

import androidx.room.RoomDatabase

/*@Database(
    version = 1,
    entities = []
)*/
/*TODO wrapper over shell magisk may be possible*/
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val NAME = "database"
    }

}