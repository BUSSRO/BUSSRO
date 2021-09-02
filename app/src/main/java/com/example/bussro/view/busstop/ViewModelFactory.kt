package com.example.bussro.view.busstop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient

/**
 * [ViewModelFactory]
 * NearbyBusStopViewModel 객체 생성을 위한 Custom Factory
 */

class ViewModelFactory(
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NearbyBusStopViewModel::class.java)) {
            NearbyBusStopViewModel(fusedLocationClient) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}