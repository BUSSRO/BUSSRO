package com.example.bussro.view.busstop

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch

/**
 * [NearbyBusStopViewModel]
 * NearbyBusStopActivity 의 ViewModel
 */

class NearbyBusStopViewModel (
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {
//    var loadingLiveData = MutableLiveData<Boolean>()  // 로딩바

    /* 사용자 주변 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestNearbyBusStop() {
//        loadingLiveData.value = true  // 로딩 시작

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("test", "위도 : ${location.latitude}, 경도 : ${location.longitude}")

                    viewModelScope.launch {
                        // TODO: 서버 데이터 받아오기
//                        loadingLiveData.postValue(false)  // 로딩 끝
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
//                loadingLiveData.postValue(false)
                Log.d("test", "requestNearbyBusStop: 위치 정보 받아올 수 없음")
            }
    }
}