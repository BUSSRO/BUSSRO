package com.example.bussro.data

/**
 * [NearbyBusStopData]
 * 공공데이터 OpenAPI (좌표기반 근접 정류소 조회) 의 Response Message 를 받을 data class
 *
 * @param arsId 정류소고유번호
 * @param stationNm 정류소명
 * @param dist 거리(m)
 */

data class NearbyBusStopData(
    var arsId: String?,
    var stationNm: String?,
    var dist: Double?
)