package com.example.bussro.model.db.dao

import androidx.room.*
import com.example.bussro.model.db.entity.History

/**
 * [HistoryDao]
 * History table access 에 사용되는 메서드
 */

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Delete
    suspend fun delete(history: History)
}