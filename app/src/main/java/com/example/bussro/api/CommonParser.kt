package com.example.bussro.api

import android.util.Xml
import com.example.bussro.data.NearbyBusStops
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URL

class CommonParser {
    // CHECK: 제네릭타입으로 함수 선언하면 Parser 묶을 수 있을 것 같음 List<T>

    /* API 의 Response Message 가져오기 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun loadXmlFromNetwork(urlString: String): List<NearbyBusStops> {
        return downloadUrl(urlString)?.use { stream ->
            NearbyBusStopParser().parse(stream)
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

    inner class Parser() {
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

        // CHECK: 위 : 공통적으로 적용되는 사항, 아래 : API 마다 다르게 적용해줄 사항

        /* API 의 Response Message 중 itemList 태그를 파싱 */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun parseItemList(parser: XmlPullParser) : NearbyBusStops {
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
}