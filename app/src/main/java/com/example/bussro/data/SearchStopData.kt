package com.example.bussro.data

/**
 * [SearchStopData]
 * 서울특별시_정류조정보조회 서비스 中 정류소 명칭 검색의 Response Message 를 받을 data class
 *
 * @param stNm 정류소명
 * @param tmX 경도
 * @param tmY 위도
 */

data class SearchStopData (
    var stNm: String?,
    var tmX: Double?,
    var tmY: Double?
)