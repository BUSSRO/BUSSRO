package com.example.bussro.model.repository

import android.app.Application
import com.example.bussro.model.db.HistoryDatabase
import com.example.bussro.model.db.entity.History
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [HistoryRepository]
 * HistoryViewModel 과 HistoryDB 사이를 중재할 repository
 */

class HistoryRepository(application: Application) {
    private var historyDatabase = HistoryDatabase.getInstance(application)!!
    private var historyDao = historyDatabase.historyDao()
    private var histories = historyDao.getAll()

    /* ViewModel 에서 DB 접근시 수행할 함수 */
    fun getAll() = histories

    /* 새로운 히스토리 입력 */
    fun insert(history: History) {
        CoroutineScope(Dispatchers.IO).launch {
            historyDao.insert(history)
        }
    }

    /* 특정 히스토리 삭제 */
    fun delete(history: History) {
        CoroutineScope(Dispatchers.IO).launch {
            historyDao.delete(history)
        }
    }

    /* 전체 히스토리 삭제 */
    fun deleteAll() {
        CoroutineScope(Dispatchers.IO).launch {
            historyDao.deleteAll()
        }
    }
}