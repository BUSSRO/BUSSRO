package com.example.bussro.api

import android.util.Xml
import com.example.bussro.data.BusListData
import com.example.bussro.data.NearbyBusStopData
import com.example.bussro.data.SearchStopData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URL

/**
 * [BusAPI]
 * 공공데이터 요청 API
 */

class BusAPI(private val type: String) {
    private val ns: String? = null

    /* API 의 Response Message 가져오기 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun <T> loadXmlFromNetwork(urlString: String): List<T> {
        return downloadUrl(urlString)?.use { stream ->
            parse(stream)
        } ?: emptyList()
    }

    /* url 로 부터 InputStream 받기 */
    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        val url = URL(urlString)

        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // query 시작
            connect()
            inputStream
        }
    }

    /* API 의 Response Message 를 파싱한 결과를 리턴함 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun <T> parse(inputStream: InputStream): List<T> {
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
    private fun <T> parseServiceResult(parser: XmlPullParser): List<T> {
        val nearbyBusStops = mutableListOf<T>()
        parser.require(XmlPullParser.START_TAG, ns, "ServiceResult")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "msgBody") {
                parseMsgBody<T>(parser).let { data ->
                    nearbyBusStops.addAll(data)
                }
            } else {  // 원하지 않는 태그(comMsgHeader, msgHeader) 스킵
                skip(parser)
            }
        }
        return nearbyBusStops
    }

    /* API 의 Response Message 중 msgBody 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun <T> parseMsgBody(parser: XmlPullParser): List<T> {
        val result = mutableListOf<T>()
        parser.require(XmlPullParser.START_TAG, ns, "msgBody")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "itemList") {
                when (type) {
                    "NearbyBusStopData" -> {
                        result.add(parseNearbyBusStopItemList(parser) as T)
                    }
                    "SearchStopData" -> {
                        result.add(parseSearchStopItemList(parser) as T)
                    }
                    "BusListData" -> {
                        result.add(parseBusListItemList(parser) as T)
                    }
                }
            } else {  // 원하지 않는 태그 스킵
                skip(parser)
            }
        }
        return result
    }

    /* BusList 데이터 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseBusListItemList(parser: XmlPullParser) : BusListData {
        var arrmsg1: String? = null
        var arrmsg2: String? = null
        var rtNm: String? = null

        parser.require(XmlPullParser.START_TAG, ns, "itemList")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "arrmsg1" -> arrmsg1 = readText(parser, "arrmsg1")
                "arrmsg2" -> arrmsg2 = readText(parser, "arrmsg2")
                "rtNm" -> rtNm = readText(parser, "rtNm")
                else -> skip(parser)
            }
        }

        return BusListData(arrmsg1, arrmsg2, rtNm)
    }

    /* API 의 Response Message 중 itemList 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseNearbyBusStopItemList(parser: XmlPullParser) : NearbyBusStopData {
        var arsId: String? = null
        var stationNm: String? = null
        var dist: Double? = null

        parser.require(XmlPullParser.START_TAG, ns, "itemList")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "arsId" -> arsId = readText(parser, "arsId")
                "stationNm" -> stationNm = readText(parser, "stationNm")
                "dist" -> dist = readText(parser, "dist").toDouble()
                else -> skip(parser)
            }
        }

        return NearbyBusStopData(arsId, stationNm, dist)
    }

    /* API 의 Response Message 중 itemList 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseSearchStopItemList(parser: XmlPullParser) : SearchStopData {
        var stNm: String? = null
        var tmX: Double? = null
        var tmY: Double? = null
        var arsId: String? = null

        parser.require(XmlPullParser.START_TAG, ns, "itemList")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "stNm" -> stNm = readText(parser, "stNm")
                "tmX" -> tmX = readText(parser, "tmX").toDouble()
                "tmY" -> tmY = readText(parser, "tmY").toDouble()
                "arsId" -> arsId = readText(parser, "arsId")
                else -> skip(parser)
            }
        }

        return SearchStopData(stNm, tmX, tmY, arsId)
    }

    /* 태그에서 value 를 추출함 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readText(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)

        var text = ""
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.text
            parser.nextTag()
        }

        parser.require(XmlPullParser.END_TAG, ns, tag)
        return text
    }

    /* 원하지 않는 태그 스킵 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun skip(parser: XmlPullParser) {
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
