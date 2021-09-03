package com.example.bussro.view.busstop

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.*
import com.example.bussro.BuildConfig
import com.example.bussro.api.NearbyBusStopAPI
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
                    GetNearbyBusStop().execute(location.longitude.toString(), location.latitude.toString())  // API 데이터 가져오기
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
//                loadingLiveData.postValue(false)
                Log.d("test", "requestNearbyBusStop: 위치 정보 받아올 수 없음")
            }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetNearbyBusStop : AsyncTask<String, Void, String>() {
        /* parameter : 경도(tmX), 위도(tmY) */
        override fun doInBackground(vararg params: String?): String {
            val baseUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?"
            val tmX = params[0]
            val tmY = params[1]
            val serviceKey = BuildConfig.NEARBY_BUS_STOP_API_KEY
            val urlString = baseUrl + "tmX=" + tmX + "&tmY=" + tmY + "&radius=300" + "&serviceKey=" + serviceKey
            val data = NearbyBusStopAPI().loadXmlFromNetwork(urlString)
            Log.d("test", "doInBackground: $data ")

            // TODO: result 를 RecyclerView 의 data 로 넣어주기

            return ""  // CHECK: return nothing
        }

        override fun onPostExecute(result: String?) {
            // loadingLiveData.postValue(false)  // 로딩 끝
        }
    }
}