package com.youreye.bussro.model.network.api

import com.youreye.bussro.model.network.response.BusStopData
import com.youreye.bussro.model.network.response.SearchedBusStopData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * [StationInfoAPI]
 * 서울특별시_정류소정보조회 서비스
 * https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15000303
 */

interface StationInfoAPI {

    /* 좌표기반 근접 정류소 조회 */
    @GET("getStationByPos")
    fun getStationByPos(
        @Query("serviceKey") serviceKey: String,
        @Query("tmX") tmx: String,
        @Query("tmY") tmY: String,
        @Query("radius") radius: String = "500",
        @Query("resultType") resultType: String = "json"
    ): Call<BusStopData>

    /* 검색어가 포함된 정류소 명칭을 조회 */
    @GET("getStationByName")
    fun getStationByName(
        @Query("serviceKey") serviceKey: String,
        @Query("stSrch") stSrch: String,
        @Query("resultType") resultType: String = "json"
    ): Call<SearchedBusStopData>

    companion object ServiceImpl {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/"

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(StationInfoAPI::class.java)
    }
}