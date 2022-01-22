package com.youreye.bussro.model.network.response

import com.google.gson.annotations.SerializedName

/**
 * [CustomBusListData]
 *
 * @param adirection 방향
 * @param arrmsg1 첫 번째 도착 예정 버스의 도착 정보 메시지
 * @param arrmsg2 두 번째 도착 예정 버스의 도착 정보 메시지
 * @param rtNm 노선명
 * @param sectNm 구간명
 * @param nxtStn 다음정류장순번
 * @param stNm 정류소명
 * @param isExpanded RecyclerView 확장여부
 */

data class CustomBusListData (
    val adirection: String,
    val arrmsg1: String,
    val arrmsg2: String,
    val rtNm: String,
    val sectNm: String,
    val nxtStn: String,
    val stNm: String,
    val arsId: String,
    val isExpanded: Boolean = false
)