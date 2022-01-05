package com.youreye.bussro.model.repository

import com.youreye.bussro.model.db.dao.HistoryDao
import com.youreye.bussro.model.db.entity.History
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [HistoryRepository]
 * HistoryViewModel 과 HistoryDB 사이를 중재할 repository
 */

@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    /* History DB 에 저장된 모든 데이터 불러오기 */
    fun getAll() = historyDao.getAll()

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