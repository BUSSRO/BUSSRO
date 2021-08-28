package com.example.bussro.data

/**
 * [BusListRvData]
 * BusListActivity 의 RecyclerView 아이템 데이터 값을 담는 클래스
 * @param number 버스 번호
 * @param info 버스 위치 정보
 *
 * @author 윤주연(otu165)
 */

data class BusListRvData (
    var number: String = "3317",
    var info: String = "전 정류장"
)