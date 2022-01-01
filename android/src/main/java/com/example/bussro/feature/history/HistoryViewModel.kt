package com.example.bussro.feature.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.bussro.model.db.entity.History
import com.example.bussro.model.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * [HistoryViewModel]
 * HistoryActivity 의 ViewModel
 */

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {
    private val histories = repository.getAll()

    fun getAll() = histories

    fun insert(history: History) = repository.insert(history)

    fun delete(history: History) = repository.delete(history)

    fun deleteAll() = repository.deleteAll()
}