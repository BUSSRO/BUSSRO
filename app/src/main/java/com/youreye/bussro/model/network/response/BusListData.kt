package com.youreye.bussro.model.network.response

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.SerializedName

/**
 * [BusListData]
 * 서울특별시_정류소정보조회 서비스 中 버스도착정보목록의 response message 를 담을 data class
 */

data class BusListData(
    @SerializedName("comMsgHeader")
    val comMsgHeader: ComMsgHeader,
    @SerializedName("msgHeader")
    val msgHeader: MsgHeader,
    @SerializedName("msgBody")
    val msgBody: MsgBody,
) {
    data class MsgBody(
        @SerializedName("itemList")
        val itemList: List<BusList>
    ) {
        /**
         * @param adirection 방향
         * @param arrmsg1 첫 번째 도착 예정 버스의 도착 정보 메시지
         * @param arrmsg2 두 번째 도착 예정 버스의 도착 정보 메시지
         * @param rtNm 노선명
         * @param sectNm 구간명
         * @param nxtStn 다음정류장순번
         * @param stNm 정류소명
         * @param arsId 정류소고유번호
         * @param routeType 노선유형
         * @param firstTm 첫차시간
         * @param lastTm 막차시간
         * @param term 배차간격
         *
         * @property isExpanded RecyclerView expand or collapse
         */

        data class BusList (
            @SerializedName("adirection")
            val adirection: String,
            @SerializedName("arrmsg1")
            val arrmsg1: String,
            @SerializedName("arrmsg2")
            val arrmsg2: String,
            @SerializedName("rtNm")
            val rtNm: String,
            @SerializedName("sectNm")
            val sectNm: String,
            @SerializedName("nxtStn")
            val nxtStn: String,
            @SerializedName("stNm")
            val stNm: String,
            @SerializedName("arsId")
            val arsId: String,
            @SerializedName("routeType")
            val routeType: Int,
            @SerializedName("firstTm")
            val firstTm: String,
            @SerializedName("lastTm")
            val lastTm: String,
            @SerializedName("term")
            val term: String,
        ) {
            var isExpanded: Boolean = false
        }
    }

    data class ComMsgHeader(
        @SerializedName("errMsg")
        val errMsg: Any,
        @SerializedName("requestMsgID")
        val requestMsgID: Any,
        @SerializedName("responseMsgID")
        val responseMsgID: Any,
        @SerializedName("responseTime")
        val responseTime: Any,
        @SerializedName("returnCode")
        val returnCode: Any,
        @SerializedName("successYN")
        val successYN: Any
    )

    data class MsgHeader(
        @SerializedName("headerCd")
        val headerCd: String,
        @SerializedName("headerMsg")
        val headerMsg: String,
        @SerializedName("itemCount")
        val itemCount: Int
    )
}

/* DataBinding_노선명 */
@BindingAdapter("rtNm")
fun setBusNumber(txt: TextView, rtNm: String) {
    txt.text = rtNm
}

/* DataBinding_도착 정보 메시지 */
@BindingAdapter("arrmsg1")
fun setBusInfo(txt: TextView, arrmsg1: String) {
    txt.text = arrmsg1
}

/* DataBinding_방향 */
@SuppressLint("SetTextI18n")
@BindingAdapter("adirection")
fun setAdirection(txt: TextView, adirection: String) {
    txt.text = "$adirection 방향"
}

/* DataBinding_배차간격 */
@SuppressLint("SetTextI18n")
@BindingAdapter("term")
fun setTerm(txt: TextView, term: String) {
    txt.text = "${term}분"
}