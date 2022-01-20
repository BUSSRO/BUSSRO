package com.youreye.bussro.feature.busstop

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.*
import com.youreye.bussro.BuildConfig
import com.youreye.bussro.model.network.api.BusAPI
import com.youreye.bussro.model.network.response.NearbyBusStopData
import com.youreye.bussro.model.network.response.SearchStopData
import com.youreye.bussro.util.LocationToDistance
import com.youreye.bussro.util.logd
import com.google.android.gms.location.FusedLocationProviderClient
import com.youreye.bussro.model.network.api.StationInfoAPI
import com.youreye.bussro.model.network.response.BusStopData
import com.youreye.bussro.model.network.response.SearchedBusStopData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * [BusStopViewModel]
 * NearbyBusStopActivity 의 ViewModel
 */

@HiltViewModel
class BusStopViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
) : ViewModel() {
    var busStopsLiveData = MutableLiveData<List<BusStopData.MsgBody.BusStop>>()
    var loadingLiveData = MutableLiveData<Boolean>()  // 로딩

    /* 사용자 주변 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestNearbyBusStop() {
        loadingLiveData.value = true

        // 사용자 위치 받기
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
                            // API 데이터 가져오기
                            requestBusStopData(
                                location.longitude.toString(),
                                location.latitude.toString()
                            )
                        }
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                loadingLiveData.postValue(false)
            }
    }

    /* 공공데이터 응답 가져오기, parameter : 인증키(serviceKey), 경도(tmX), 위도(tmY), 거리(radius) */
    private fun requestBusStopData(tmX: String, tmY: String) {
        val call = StationInfoAPI.api.getStationByPos(
            BuildConfig.API_KEY,
            tmX,
            tmY
        )

        call.enqueue(object : retrofit2.Callback<BusStopData> {
            override fun onResponse(call: Call<BusStopData>, response: Response<BusStopData>) {
                if (response.isSuccessful) {
                    response.body()?.msgBody?.busStopList?.apply {
                        busStopsLiveData.postValue(this)
                    }
                } else {
                    busStopsLiveData.postValue(listOf())
                }
                loadingLiveData.postValue(false)  // 로딩 끝
            }

            override fun onFailure(call: Call<BusStopData>, t: Throwable) {
                logd("onFailure: $t")

                busStopsLiveData.postValue(listOf())
                loadingLiveData.postValue(false)  // 로딩 끝
            }
        })
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
                    requestSearchedBusStopData(stSrch, location)
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                loadingLiveData.postValue(false)
            }
    }

    /* 공공데이터 응답 가져오기, parameter : 인증키(serviceKey), 검색어(stSrch) */
    private fun requestSearchedBusStopData(stSrch: String, location: Location) {
        val call = StationInfoAPI.api.getStationByName(
            BuildConfig.API_KEY,
            stSrch
        )

        call.enqueue(object : Callback<SearchedBusStopData> {
            override fun onResponse(
                call: Call<SearchedBusStopData>,
                response: Response<SearchedBusStopData>
            ) {
                if (response.isSuccessful) {
                    val dataList = response.body()?.msgBody?.itemList

                    /* 데이터 가공 */

                    // data 의 위도, 경도를 현재 사용자의 위도, 경도와 비교해 거리 얻어낸 후
                    // List<BusStopData.MsgBody.BusStop> 타입으로 변경하기


                    if (dataList != null) {
                        val theData = mutableListOf<BusStopData.MsgBody.BusStop>()

                        for (data in dataList) {
                            // 서울특별시 소재의 버스정류장이 맞는지 확인
                            val city = geocoder.getFromLocation(
                                data.tmY.toDouble(),
                                data.tmX.toDouble(),
                                1
                            )

                            if (!city.isNullOrEmpty() && city[0].adminArea != "서울특별시") {
                                continue
                            }

                            // 데이터 입력
                            theData.add(
                                BusStopData.MsgBody.BusStop(
                                    data.arsId,
                                    LocationToDistance().distance(
                                        location.longitude, location.latitude,
                                        data.tmX.toDouble(), data.tmY.toDouble(),
                                        "meter"
                                    ),
                                    data.stNm
                                )
                            )
                        }

                        theData.sortBy { data -> data.dist }

                        busStopsLiveData.postValue(theData)
                        loadingLiveData.postValue(false)  // 로딩 끝
                    } else {
                        busStopsLiveData.postValue(listOf())
                        loadingLiveData.postValue(false)  // 로딩 끝
                    }
                } else {
                    busStopsLiveData.postValue(listOf())
                    loadingLiveData.postValue(false)  // 로딩 끝
                }
            }

            override fun onFailure(call: Call<SearchedBusStopData>, t: Throwable) {
                busStopsLiveData.postValue(listOf())
                loadingLiveData.postValue(false)  // 로딩 끝
            }
        })
    }
}