package com.example.bussro.data

/**
 * [NearbyBusStopRvData]
 * NearbyBusStopActivity 의 RecyclerView 에 사용될 데이터
 * 공공데이터 OpenAPI (좌표기반 근접 정류소 조회) 의 Response Message 를 받을 data class
 */

data class NearbyBusStopRvData(
    val msgHeader: List<NearbyBusStopHeader>,
    val msgBody: List<NearbyBusStops>
)

/**
 * @param headerCd 오류코드
 */

data class NearbyBusStopHeader (
    val headerCd: Int
)

/**
 * @param stationNm 정류소명
 * @param dist 거리(m)
 */

data class NearbyBusStops (
    var stationNm: String?,
    var dist: Double?
)