package com.example.bussro.di.module

import android.content.Context
import androidx.room.Room
import com.example.bussro.model.db.HistoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HistoryModule {

    @Provides
    fun getHistoryDao(historyDatabase: HistoryDatabase) =
        historyDatabase.historyDao()

    @Provides
    @Singleton
    fun getHistoryDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            HistoryDatabase::class.java,
            "history"
        )
            .fallbackToDestructiveMigration()  // db 갱신시 새 table 생성
            .build()
}