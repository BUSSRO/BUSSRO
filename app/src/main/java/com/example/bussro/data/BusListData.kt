package com.example.bussro.data

/**
 * [BusListData]
 * 서울특별시_정류소정보조회 서비스 中 버스도착정보목록의 response message 를 담을 data class
 *
 * @param arrmsg1 첫 번째 도착 예정 버스의 도착 정보 메시지
 * @param arrmsg2 두 번째 도착 예정 버스의 도착 정보 메시지
 * @param rtNm 노선명
 */

data class BusListData (
    var arrmsg1: String?,
    var arrmsg2: String?,
    var rtNm: String?
)