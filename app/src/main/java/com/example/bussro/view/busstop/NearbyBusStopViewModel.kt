package com.example.bussro.view.busstop

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.*
import com.example.bussro.BuildConfig
import com.example.bussro.api.NearbyBusStopAPI
import com.example.bussro.data.NearbyBusStops
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.*

/**
 * [NearbyBusStopViewModel]
 * NearbyBusStopActivity 의 ViewModel
 */

class NearbyBusStopViewModel(
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {
    var busStopsLiveData = MutableLiveData<List<NearbyBusStops>>()
    var loadingLiveData = MutableLiveData<Boolean>()  // 로딩

    /* 사용자 주변 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestNearbyBusStop() {
        loadingLiveData.value = true
        // 1. 사용자 위치 받기
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("test", "위도 : ${location.latitude}, 경도 : ${location.longitude}")
                    // 2. API 데이터 가져오기
                    viewModelScope.launch {
                        val data = getApiData(
                            location.longitude.toString(),
                            location.latitude.toString(),
                            "300"
                        )

                        busStopsLiveData.postValue(data)
                        loadingLiveData.postValue(false)  // 로딩 끝

                        // TODO: TTS 로 로딩 끝 안내하기
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                loadingLiveData.postValue(false)
            }
    }

    /* 공공데이터 응답 가져오기, parameter : 경도(tmX), 위도(tmY), 거리(radius) */
    private suspend fun getApiData(tmX: String, tmY: String, radius: String): List<NearbyBusStops> {
        val urlString =
            BASE_URL + "serviceKey=" + SERVICE_KEY + "&tmX=" + tmX + "&tmY=" + tmY + "&radius=" + radius

        return withContext(Dispatchers.IO) {
            NearbyBusStopAPI().loadXmlFromNetwork(urlString)
        }
    }

    companion object {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?"
        private const val SERVICE_KEY = BuildConfig.NEARBY_BUS_STOP_API_KEY
    }
}