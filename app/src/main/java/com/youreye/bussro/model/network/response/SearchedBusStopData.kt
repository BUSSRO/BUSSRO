package com.youreye.bussro.model.network.response


import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.SerializedName

/**
 * [SearchStopData]
 * 서울특별시_정류조정보조회 서비스 中 정류소 명칭 검색의 Response Message 를 받을 data class
 */

data class SearchedBusStopData(
    @SerializedName("comMsgHeader")
    val comMsgHeader: ComMsgHeader,

    @SerializedName("msgHeader")
    val msgHeader: MsgHeader,

    @SerializedName("msgBody")
    val msgBody: MsgBody,
) {
    data class MsgBody(
        @SerializedName("itemList")
        val itemList: List<searchedBusStop>
    ) {
        /**
         * @param stNm 정류소명
         * @param tmX 경도
         * @param tmY 위도
         * @param arsId 정류소고유번호
         */

        data class searchedBusStop(
            @SerializedName("arsId")
            val arsId: String,
            @SerializedName("posX")
            val posX: String,
            @SerializedName("posY")
            val posY: String,
            @SerializedName("stId")
            val stId: String,
            @SerializedName("stNm")
            val stNm: String,
            @SerializedName("tmX")
            val tmX: String,
            @SerializedName("tmY")
            val tmY: String
        )
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

/* DataBinding_정류소고유번호 */
@BindingAdapter("arsId")
fun setDist(txt: TextView, arsId: String) {
    txt.text = arsId
}