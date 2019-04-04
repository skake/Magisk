package com.topjohnwu.magisk.data.database

import androidx.room.Dao
import androidx.room.Query
import com.skoumal.teanity.database.BaseDao
import com.topjohnwu.magisk.model.entity.MagiskModule


@Dao
interface RepositoryDao : BaseDao<MagiskModule.Repository> {

    @Query("DELETE FROM repos")
    override fun deleteAll()

    @Query("SELECT * FROM repos")
    override fun fetchAll(): List<MagiskModule.Repository>

}
