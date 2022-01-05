package com.youreye.bussro.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.youreye.bussro.model.db.entity.History

/**
 * [HistoryDao]
 * History table access 에 사용되는 메서드
 */

@Dao
interface HistoryDao {
    /* 모든 히스토리 조회(시간 내림차순) */
    @Query("SELECT * FROM history ORDER BY date DESC")
    fun getAll(): LiveData<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Delete
    suspend fun delete(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()
}