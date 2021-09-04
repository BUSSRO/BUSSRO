package com.example.bussro.view.buslist

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bussro.BuildConfig
import com.example.bussro.api.API
import com.example.bussro.data.BusListData
import com.example.bussro.view.businfo.BusInfoActivity
import com.example.bussro.view.sign.SignActivity
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
                 API("BusListData").loadXmlFromNetwork<BusListData>(urlString)
            }


            Log.d("test", "requestBusList: $data")

            busListLiveData.postValue(data)
            loadingLiveData.postValue(false)
        }
    }

    /* onClick */
    fun onClick(v: View) {
        val intent = Intent(v.context, BusInfoActivity::class.java)
            .putExtra("stationNm", stationNm)
        Log.d("test", "onClick: $stationNm")
        v.context.startActivity(intent)
    }

    companion object {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?"
        private const val SERVICE_KEY = BuildConfig.NEARBY_BUS_STOP_API_KEY
    }
}