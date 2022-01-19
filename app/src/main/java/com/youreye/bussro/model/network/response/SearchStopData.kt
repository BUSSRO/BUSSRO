package com.youreye.bussro.model.network.response

import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * [SearchStopData]
 * 서울특별시_정류조정보조회 서비스 中 정류소 명칭 검색의 Response Message 를 받을 data class
 *
 * @param stNm 정류소명
 * @param tmX 경도
 * @param tmY 위도
 * @param arsId 정류소고유번호
 */

data class SearchStopData (
    var stNm: String?,
    var tmX: Double?,
    var tmY: Double?,
    var arsId: String?
)

/* DataBinding_정류소고유번호 */
@BindingAdapter("arsId")
fun setDist(txt: TextView, arsId: String) {
    txt.text = arsId
}