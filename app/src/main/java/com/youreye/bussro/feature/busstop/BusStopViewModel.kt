package com.youreye.bussro.feature.busstop

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.*
import com.youreye.bussro.BuildConfig
import com.youreye.bussro.util.LocationToDistance
import com.youreye.bussro.util.logd
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.youreye.bussro.di.App
import com.youreye.bussro.model.network.api.StationInfoAPI
import com.youreye.bussro.model.network.response.BusStopData
import com.youreye.bussro.model.network.response.SearchedBusStopData
import com.youreye.bussro.util.NetworkConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Collectors
import javax.inject.Inject

/**
 * [BusStopViewModel]
 * NearbyBusStopActivity 의 ViewModel
 */

@HiltViewModel
class BusStopViewModel @Inject constructor(
    private val geocoder: Geocoder,
) : ViewModel() {
    var busStopsLiveData = MutableLiveData<List<BusStopData.MsgBody.BusStop>>()
    var loadingLiveData = MutableLiveData<Boolean>()
    var failReason = MutableLiveData<String>()
    var searchTerm = ""

    /* 사용자 주변 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestBusStop(location: Location?) {
        loadingLiveData.value = true

        // 네트워크 확인
        if (!NetworkConnection.isConnected()) {
            loadFail("network")
            return
        }

        // 검색어 초기화
        searchTerm = ""


        if (location != null) {
            // 위치가 있다면 (위도 : location.latitude, 경도 : location.longitude)
            viewModelScope.launch {
                // 사용자의 위치가 서울특별시인지 확인
                val city = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                if (!city.isNullOrEmpty() && city[0].adminArea == "서울특별시") {
                    // API 호출
                    requestBusStopData(
                        location.longitude.toString(),
                        location.latitude.toString()
                    )
                } else {
                    loadFail("no_result")
                }
            }
        } else {
            // 위치 정보 받아올 수 없음
            loadFail("location")
        }
    }

    /* 좌표기반 근접 정류소 조회 API 호출 */
    private fun requestBusStopData(tmX: String, tmY: String) {
        val call = StationInfoAPI.api.getStationByPos(
            BuildConfig.API_KEY,
            tmX,
            tmY
        )

        call.enqueue(object : Callback<BusStopData> {
            override fun onResponse(call: Call<BusStopData>, response: Response<BusStopData>) {
                if (response.isSuccessful) {
                    // 성공
                    val busStopList = response.body()?.msgBody?.busStopList

                    if (busStopList != null) {
                        // arsId == 0 인 정류장 거르기
                        val data = busStopList.stream().filter {
                            it.arsId.toInt() != 0
                        }.collect(Collectors.toList())

                        busStopsLiveData.postValue(data)
                        loadingLiveData.postValue(false)
                    } else {
                        loadFail("no_result")
                    }
                } else {
                    // 실패
                    loadFail("no_result")
                }
            }

            override fun onFailure(call: Call<BusStopData>, t: Throwable) {
                logd("onFailure: $t")
                loadFail("no_result")
            }
        })
    }

    /* 사용자 검색 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestSearchedBusStop(stSrch: String, location: Location?) {
        loadingLiveData.value = true

        // 네트워크 확인
        if (!NetworkConnection.isConnected()) {
            loadFail("network")
            return
        }

        // 검색어 저장
        searchTerm = stSrch

        if (location != null) {
            // 위치가 있다면 (위도 : location.latitude, 경도 : location.longitude)
            requestSearchedBusStopData(stSrch, location)
        } else {
            // 위치 정보 받아올 수 없음
            loadFail("location")
        }
    }

    /* 검색어가 포함된 정류소 명칭을 조회 API 호출 */
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
                    // 성공
                    val searchedBusStopList = response.body()?.msgBody?.itemList

                    if (searchedBusStopList != null) {
                        val data = mutableListOf<BusStopData.MsgBody.BusStop>()

                        for (searchedBusStop in searchedBusStopList) {
                            // 서울특별시 소재의 버스정류장이 맞는지 확인
                            val city = geocoder.getFromLocation(
                                searchedBusStop.tmY.toDouble(),
                                searchedBusStop.tmX.toDouble(),
                                1
                            )

                            // arsId == 0 인지 확인
                            if (searchedBusStop.arsId.toInt() == 0) {
                                continue
                            }

                            if (!city.isNullOrEmpty() && city[0].adminArea != "서울특별시") {
                                continue
                            }

                            // 버스정류장과 사용자 사이 거리 계산
                            val dist = LocationToDistance().distance(
                                location.longitude, location.latitude,
                                searchedBusStop.tmX.toDouble(), searchedBusStop.tmY.toDouble(),
                                "meter"
                            )

                            // 데이터 저장
                            data.add(
                                BusStopData.MsgBody.BusStop(
                                    searchedBusStop.arsId,
                                    dist,
                                    searchedBusStop.stNm
                                )
                            )
                        }

                        // 거리순으로 정렬
                        data.sortBy { data -> data.dist }

                        if (data.isNotEmpty()) {
                            busStopsLiveData.postValue(data)
                            loadingLiveData.postValue(false)
                        } else {
                            loadFail("no_search_result")
                        }
                    } else {
                        loadFail("no_search_result")
                    }
                } else {
                    loadFail("no_search_result")
                }
            }

            override fun onFailure(call: Call<SearchedBusStopData>, t: Throwable) {
                loadFail("no_search_result")
            }
        })
    }


    /* data == null 인 경우 처리 */
    private fun loadFail(reason: String) {
        busStopsLiveData.postValue(listOf())
        loadingLiveData.postValue(false)
        failReason.postValue(reason)
    }
}