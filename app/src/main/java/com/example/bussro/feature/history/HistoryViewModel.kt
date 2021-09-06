package com.example.bussro.feature.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bussro.model.db.entity.History
import com.example.bussro.model.repository.HistoryRepository

/**
 * [HistoryViewModel]
 * HistoryActivity 의 ViewModel
 */

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HistoryRepository(application)
    private val histories = repository.getAll()

    fun getAll() = histories

    fun insert(history: History) = repository.insert(history)

    fun delete(history: History) = repository.delete(history)

    fun deleteAll() = repository.deleteAll()
}