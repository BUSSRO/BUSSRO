package com.example.bussro.view.busstop

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.*
import com.example.bussro.BuildConfig
import com.example.bussro.api.NearbyBusStopAPI
import com.example.bussro.api.TestAPI
import com.example.bussro.data.NearbyBusStops
import com.example.bussro.data.TestData
import com.example.bussro.util.LocationToDistance
import com.example.bussro.view.findstation.FindStationActivity
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.*
import kotlin.math.round

/**
 * [NearbyBusStopViewModel]
 * NearbyBusStopActivity 의 ViewModel
 */

class NearbyBusStopViewModel(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val startForResult: ActivityResultLauncher<Intent>
) : ViewModel() {
    var busStopsLiveData = MutableLiveData<List<NearbyBusStops>>()
    var loadingLiveData = MutableLiveData<Boolean>()  // 로딩

    /* handle click event */
    fun onclick(view: View) {
        val intent = Intent(view.context, FindStationActivity::class.java)
//        view.context.startActivity(intent)
        startForResult.launch(intent)
    }

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

                        // convert meter to kilometer
                        for (d in data) {
                            d.dist = d.dist?.div(1000)
                        }

                        busStopsLiveData.postValue(data)
                        loadingLiveData.postValue(false)  // 로딩 끝
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                loadingLiveData.postValue(false)
            }
    }

    /* 공공데이터 응답 가져오기, parameter : 인증키(serviceKey), 경도(tmX), 위도(tmY), 거리(radius) */
    private suspend fun getApiData(tmX: String, tmY: String, radius: String): List<NearbyBusStops> {
        val urlString =
            BASE_URL + "serviceKey=" + SERVICE_KEY + "&tmX=" + tmX + "&tmY=" + tmY + "&radius=" + radius

        return withContext(Dispatchers.IO) {
            NearbyBusStopAPI().loadXmlFromNetwork(urlString)
        }
    }

    /* 사용자 검색 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestSearchedBusStop(stSrch: String) {
        Log.d("test", "requestSearchedBusStop: 사용자 검색 ==> $stSrch 정류장")
        loadingLiveData.value = true
        // 1. 사용자 위치 받기
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // 2. API 데이터 가져오기
                    viewModelScope.launch {
                        val data = getTestData(stSrch)

                        // TestData 의 위도, 경도를 현재 사용자의 위도, 경도와 비교해 거리 얻어낸 후
                        // List<NearbyBusStops> 타입으로 변경하기
                        val theData = mutableListOf<NearbyBusStops>()

                        for (d in data) {
                            theData.add(
                                NearbyBusStops(
                                    d.stNm,
                                    round(LocationToDistance().distance(  // 반올림
                                        location.longitude, location.latitude,
                                        d.tmX!!, d.tmY!!,
                                        "kilo"
                                    ) * 1000) / 1000
                                )
                            )
                        }

                        theData.sortBy { data -> data.dist }

                        busStopsLiveData.postValue(theData)
                        loadingLiveData.postValue(false)  // 로딩 끝
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                loadingLiveData.postValue(false)
            }
    }

    /* 공공데이터 응답 가져오기, parameter : 인증키(serviceKey), 검색어(stSrch) */
    private suspend fun getTestData(stSrch: String): List<TestData> {
        val urlString = TEST_BASE_URL + "serviceKey=" + SERVICE_KEY + "&stSrch=" + stSrch

        return withContext(Dispatchers.IO) {
            TestAPI().loadXmlFromNetwork(urlString)
        }
    }

    companion object {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?"
        private const val TEST_BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByName?"
        private const val SERVICE_KEY = BuildConfig.NEARBY_BUS_STOP_API_KEY
    }
}