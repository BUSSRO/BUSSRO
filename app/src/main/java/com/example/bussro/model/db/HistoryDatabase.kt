package com.example.bussro.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bussro.model.db.dao.HistoryDao
import com.example.bussro.model.db.entity.History

/**
 * [HistoryDatabase]
 * History 데이터베이스 객체
 */

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        private var instance: HistoryDatabase? = null

        /* return History Database*/
        fun getInstance(context: Context): HistoryDatabase? {
            if (instance == null) {
                synchronized(HistoryDatabase::class) {
                    // db 객체 생성
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryDatabase::class.java,
                        "history")
                        .fallbackToDestructiveMigration()  // db 갱신시 새 table 생성
                        .build()
                }
            }
            return instance
        }
    }
}