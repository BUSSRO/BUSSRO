package com.example.bussro.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bussro.model.db.dao.HistoryDao
import com.example.bussro.model.db.entity.History

/**
 * [HistoryDatabase]
 * History 데이터베이스 객체
 */

@Database(entities = [History::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}