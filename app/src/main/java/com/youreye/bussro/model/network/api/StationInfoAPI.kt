package com.youreye.bussro.model.network.api

import com.youreye.bussro.model.network.response.BusListData
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

    /* 좌표기반 근접 정류소 조회
    * 인증키(serviceKey), 경도(tmX), 위도(tmY), 검색거리(radius), 응답유형(resultType) */
    @GET("getStationByPos")
    fun getStationByPos(
        @Query("serviceKey") serviceKey: String,
        @Query("tmX") tmx: String,
        @Query("tmY") tmY: String,
        @Query("radius") radius: String = "500",
        @Query("resultType") resultType: String = "json"
    ): Call<BusStopData>

    /* 검색어가 포함된 정류소 명칭을 조회
    * 인증키(serviceKey), 검색어(stSrch), 응답유형(resultType) */
    @GET("getStationByName")
    fun getStationByName(
        @Query("serviceKey") serviceKey: String,
        @Query("stSrch") stSrch: String,
        @Query("resultType") resultType: String = "json"
    ): Call<SearchedBusStopData>

    /* 노선 고유번호에 해당하는 정류소 정보를 조회
    * 인증키(serviceKey), 정류소 고유번호(arsId), 응답유형(resultType) */
    @GET("getStationByUid")
    fun getBusList(
        @Query("serviceKey") serviceKey: String,
        @Query("arsId") arsId: String,
        @Query("resultType") resultType: String = "json"
    ): Call<BusListData>

    companion object ServiceImpl {
        private const val BASE_URL = "http://ws.bus.go.kr/api/rest/stationinfo/"

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(StationInfoAPI::class.java)
    }
}