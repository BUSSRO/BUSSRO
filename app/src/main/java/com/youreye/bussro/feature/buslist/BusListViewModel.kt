package com.youreye.bussro.feature.buslist

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youreye.bussro.BuildConfig
import com.youreye.bussro.model.network.api.BusAPI
import com.youreye.bussro.model.network.response.BusListData
import com.youreye.bussro.util.logd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var busListLiveData = MutableLiveData<List<BusListData>>()  // recyclerview data
    var loadingLiveData = MutableLiveData<Boolean>()  // 로딩바

    /* 정류장 버스 도착 정보 목록 요청 메서드 */
    fun requestBusList() {
        loadingLiveData.value = true
        viewModelScope.launch {
            val urlString = BASE_URL + "serviceKey=" + SERVICE_KEY + "&arsId=" + arsId
            val data = withContext(Dispatchers.IO) {
                 BusAPI("BusListData").loadXmlFromNetwork<BusListData>(urlString)
            }

            logd("requestBusList: $data")

            busListLiveData.postValue(data)
            loadingLiveData.postValue(false)
        }
    }

    companion object {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?"
        private const val SERVICE_KEY = BuildConfig.NEARBY_BUS_STOP_API_KEY
    }
}