package com.example.bussro.feature.buslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bussro.feature.nearbybusstop.ViewModelFactory

/**
 * [ViewModelFactory]
 * BusListViewModel 객체 생성을 위한 Custom Factory
 */

class ViewModelFactory (
    private val arsId: String,
    private val stationNm: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BusListViewModel::class.java)) {
            BusListViewModel(arsId, stationNm) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}