package com.youreye.bussro.model.network.response


import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.SerializedName

/**
 * [NearbyBusStopData]
 * 공공데이터 OpenAPI (좌표기반 근접 정류소 조회) 의 Response Message 를 받을 data class
 */

data class BusStopData(
    @SerializedName("comMsgHeader")
    val comMsgHeader: ComMsgHeader,

    @SerializedName("msgHeader")
    val msgHeader: MsgHeader,

    @SerializedName("msgBody")
    val msgBody: MsgBody,
) {
    data class MsgBody(
        @SerializedName("itemList")
        val busStopList: List<BusStop>
    ) {
        /**
         * @param arsId 정류소고유번호
         * @param stationNm 정류소명
         * @param dist 거리(m)
         */

        data class BusStop(
            @SerializedName("arsId")
            val arsId: String,
            @SerializedName("dist")
            val dist: Double,
            @SerializedName("gpsX")
            val gpsX: String,
            @SerializedName("gpsY")
            val gpsY: String,
            @SerializedName("posX")
            val posX: String,
            @SerializedName("posY")
            val posY: String,
            @SerializedName("stationId")
            val stationId: String,
            @SerializedName("stationNm")
            val stationNm: String,
            @SerializedName("stationTp")
            val stationTp: String
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

/* DataBinding_정류소명 */
@BindingAdapter("stationName")
fun setStationNm(txt: TextView, stationNm: String) {
    txt.text = stationNm
}

/* DataBinding_거리(기준 : km) */
@BindingAdapter("dist")
fun setDist(txt: TextView, dist: Double) {
    txt.text = String.format("%.3f km", dist.div(1000))
}