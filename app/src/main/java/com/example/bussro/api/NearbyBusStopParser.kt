package com.example.bussro.api

import android.util.Xml
import com.example.bussro.data.NearbyBusStops
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException

/**
 * [NearbyBusStopAPI]
 * 공공데이터 OpenAPI (서울특별시_정류소정보조회 서비스) XML parser
 */

class NearbyBusStopParser {
    private val ns: String? = null

    /* API 의 Response Message 를 파싱한 결과를 리턴함 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<NearbyBusStops> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser().apply {
                setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                setInput(it, null)
                nextTag()
            }
            return parseServiceResult(parser)
        }
    }

    /* API 의 Response Message 중 ServiceResult 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseServiceResult(parser: XmlPullParser) : List<NearbyBusStops> {
        val nearbyBusStops = mutableListOf<NearbyBusStops>()
        parser.require(XmlPullParser.START_TAG, ns, "ServiceResult")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "msgBody") {
                parseMsgBody(parser).let { data ->
                    nearbyBusStops.addAll(data)
                }
            }
            else {  // 원하지 않는 태그(comMsgHeader, msgHeader) 스킵
                skip(parser)
            }
        }
        return nearbyBusStops
    }

    /* API 의 Response Message 중 msgBody 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMsgBody(parser: XmlPullParser) : List<NearbyBusStops> {
        val result = mutableListOf<NearbyBusStops>()
        parser.require(XmlPullParser.START_TAG, ns, "msgBody")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "itemList") {
                parseItemList(parser).let { data ->
                    result.add(data)
                }
            }
            else {  // 원하지 않는 태그 스킵
                skip(parser)
            }
        }
        return result
    }

    /* API 의 Response Message 중 itemList 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseItemList(parser: XmlPullParser) : NearbyBusStops {
        var stationNm: String? = null
        var dist: Int? = null

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

        return NearbyBusStops(stationNm, dist)
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
    private fun readDist(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, ns, "dist")
        val dist = readText(parser).toInt()
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