package com.youreye.bussro.feature.nearbybusstop

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.lifecycle.*
import com.youreye.bussro.BuildConfig
import com.youreye.bussro.model.network.api.BusAPI
import com.youreye.bussro.model.network.response.NearbyBusStopData
import com.youreye.bussro.model.network.response.SearchStopData
import com.youreye.bussro.util.LocationToDistance
import com.youreye.bussro.util.logd
import com.google.android.gms.location.FusedLocationProviderClient
import com.youreye.bussro.util.NetworkConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * [NearbyBusStopViewModel]
 * NearbyBusStopActivity 의 ViewModel
 */

@HiltViewModel
class NearbyBusStopViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
) : ViewModel() {
    var busStopsLiveData = MutableLiveData<List<NearbyBusStopData>>()
    var loadingLiveData = MutableLiveData<Boolean>()  // 로딩

    /* 사용자 주변 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestNearbyBusStop() {
        loadingLiveData.value = true

        // 1. 사용자 위치 받기
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    logd("위도 : ${location.latitude}, 경도 : ${location.longitude}")

                    viewModelScope.launch {
                        // 사용자의 위치가 서울특별시인지 확인
                        val city = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        if (!city.isNullOrEmpty() && city[0].adminArea == "서울특별시") {
                            // 2. API 데이터 가져오기
                            val data = getApiData(
                                location.longitude.toString(),
                                location.latitude.toString(),
                                "300"
                            )

                            for (d in data) {
                                logd("stationNm: ${d.stationNm}, arsId: ${d.arsId}")
                                logd("$city")
                            }

                            busStopsLiveData.postValue(data)
                        } else {
                            busStopsLiveData.postValue(listOf())
                        }

                        loadingLiveData.postValue(false)  // 로딩 끝
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                loadingLiveData.postValue(false)
            }
    }

    /* 공공데이터 응답 가져오기, parameter : 인증키(serviceKey), 경도(tmX), 위도(tmY), 거리(radius) */
    private suspend fun getApiData(tmX: String, tmY: String, radius: String): List<NearbyBusStopData> {
        val urlString =
            BASE_URL + "getStationByPos?serviceKey=" + SERVICE_KEY + "&tmX=" + tmX + "&tmY=" + tmY + "&radius=" + radius

        return withContext(Dispatchers.IO) {
            BusAPI("NearbyBusStopData").loadXmlFromNetwork(urlString)
        }
    }

    /* 사용자 검색 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestSearchedBusStop(stSrch: String) {
        logd("requestSearchedBusStop: 사용자 검색 ==> $stSrch 정류장")
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
                        val theData = mutableListOf<NearbyBusStopData>()

                        withContext(Dispatchers.IO) {
                            for (d in data) {
                                // 서울특별시 소재의 버스정류장이 맞는지 확인
                                val city = geocoder.getFromLocation(
                                    d.tmY!!,
                                    d.tmX!!,
                                    1
                                )

                                logd("$city")

                                if (!city.isNullOrEmpty() && city[0].adminArea != "서울특별시") {
                                    continue
                                }

                                // 서울특별시 소재의 버스정류장만 데이터 입력
                                theData.add(
                                    NearbyBusStopData(
                                        d.arsId,
                                        d.stNm,
                                        LocationToDistance().distance(
                                            location.longitude, location.latitude,
                                            d.tmX!!, d.tmY!!,
                                            "meter"
                                        )
                                    )
                                )
                            }

                            theData.sortBy { data -> data.dist }

                            busStopsLiveData.postValue(theData)
                            loadingLiveData.postValue(false)  // 로딩 끝
                        }
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                loadingLiveData.postValue(false)
            }
    }

    /* 공공데이터 응답 가져오기, parameter : 인증키(serviceKey), 검색어(stSrch) */
    private suspend fun getTestData(stSrch: String): List<SearchStopData> {
        val urlString =
            BASE_URL + "getStationByName?serviceKey=" + SERVICE_KEY + "&stSrch=" + stSrch

        return withContext(Dispatchers.IO) {
            BusAPI("SearchStopData").loadXmlFromNetwork(urlString)
        }
    }

    companion object {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/"
        private const val SERVICE_KEY = BuildConfig.NEARBY_BUS_STOP_API_KEY
    }
}