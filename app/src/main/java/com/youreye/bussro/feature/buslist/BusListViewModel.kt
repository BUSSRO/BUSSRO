package com.youreye.bussro.feature.buslist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youreye.bussro.BuildConfig
import com.youreye.bussro.model.network.api.StationInfoAPI
import com.youreye.bussro.model.network.response.BusListData
import com.youreye.bussro.util.NetworkConnection
import com.youreye.bussro.util.logd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * [BusListViewModel]
 * BusListActivity 의 ViewModel
 *
 * @param arsId 정류소고유번호
 * @param stationNm 정류소명
 */

class BusListViewModel(
    private val arsId: String,
    private val stationNm: String
) : ViewModel() {
    var busListLiveData = MutableLiveData<List<BusListData.MsgBody.BusList>>()
    var loadingLiveData = MutableLiveData<Boolean>()
    var failReason = MutableLiveData<String>()

    /* 정류장 경유 노선 정보 요청 메서드 */
    fun requestBusList() {
        loadingLiveData.value = true

        // 네트워크 확인
        if (!NetworkConnection.isConnected()) {
            loadFail("network")
            return
        }

        // API 호출
        requestBusListData()
    }

    /* 노선 고유번호에 해당하는 정류소 정보를 조회 API 호출 */
    fun requestBusListData() {
        val call = StationInfoAPI.api.getBusList(
            BuildConfig.API_KEY,
            arsId
        )

        call.enqueue(object: Callback<BusListData> {
            override fun onResponse(call: Call<BusListData>, response: Response<BusListData>) {
                if (response.isSuccessful) {
                    // 성공
                    val busList = response.body()?.msgBody?.itemList

                    if (busList != null) {
                        busListLiveData.postValue(busList!!)
                        loadingLiveData.postValue(false)
                    } else {
                        loadFail("no_result")
                    }
                } else {
                    loadFail("no_result")
                }
            }

            override fun onFailure(call: Call<BusListData>, t: Throwable) {
                logd("onFailure: $t")
                loadFail("no_result")
            }
        })

    }

    /* data == null 인 경우 처리 */
    private fun loadFail(reason: String) {
        busListLiveData.postValue(listOf())
        loadingLiveData.postValue(false)
        failReason.postValue(reason)
    }

    companion object {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?"
        private const val SERVICE_KEY = BuildConfig.API_KEY
    }
}