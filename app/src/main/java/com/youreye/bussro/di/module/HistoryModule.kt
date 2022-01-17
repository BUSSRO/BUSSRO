package com.youreye.bussro.di.module

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.youreye.bussro.model.db.HistoryDatabase
import com.youreye.bussro.util.DateTypeConverter
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
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun getHistoryDatabase(@ApplicationContext context: Context, gson: Gson) =
        Room.databaseBuilder(context, HistoryDatabase::class.java, "history")
            .addTypeConverter(DateTypeConverter(gson))
            .fallbackToDestructiveMigration()  // db 갱신시 새 table 생성
            .build()
}