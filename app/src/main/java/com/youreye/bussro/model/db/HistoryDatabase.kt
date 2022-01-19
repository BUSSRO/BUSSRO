package com.youreye.bussro.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.youreye.bussro.model.db.dao.HistoryDao
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.util.DateTypeConverter

/**
 * [HistoryDatabase]
 * History 데이터베이스 객체
 */

@Database(entities = [History::class], version = 10, exportSchema = false)
@TypeConverters(value = [DateTypeConverter::class])
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}