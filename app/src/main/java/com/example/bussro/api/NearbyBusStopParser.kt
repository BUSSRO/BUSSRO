package com.example.bussro.api

import com.example.bussro.data.NearbyBusStopData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.IllegalStateException

/**
 * [NearbyBusStopParser]
 * 서울특별시_정류소정보조회서비스 中 좌표기반 근접 정류소 조회 response message parser
 */

class NearbyBusStopParser {
    private val ns: String? = null

    /* API 의 Response Message 중 itemList 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parseItemList(parser: XmlPullParser) : NearbyBusStopData {
        var stationNm: String? = null
        var dist: Double? = null

        parser.require(XmlPullParser.START_TAG, ns, "itemList")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "stationNm" -> stationNm = readStationNm(parser)
                "dist" -> dist = readDist(parser)
                else -> skip(parser)
            }
        }

        return NearbyBusStopData(stationNm, dist)
    }

    /* stationNm 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readStationNm(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "stationNm")
        val stationNm = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "stationNm")
        return stationNm
    }

    /* dist 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readDist(parser: XmlPullParser): Double {
        parser.require(XmlPullParser.START_TAG, ns, "dist")
        val dist = readText(parser).toDouble()
        parser.require(XmlPullParser.END_TAG, ns, "dist")
        return dist
    }

    /* 태그에서 value 를 추출함 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    /* 원하지 않는 태그 스킵 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}