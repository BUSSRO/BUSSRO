package com.example.bussro.view.nearbybusstop

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient

/**
 * [ViewModelFactory]
 * NearbyBusStopViewModel 객체 생성을 위한 Custom Factory
 */

class ViewModelFactory(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val startForResult: ActivityResultLauncher<Intent>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NearbyBusStopViewModel::class.java)) {
            NearbyBusStopViewModel(fusedLocationClient, startForResult) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}