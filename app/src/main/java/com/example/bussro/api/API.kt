package com.example.bussro.api

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URL

/**
 * [API]
 * 공공데이터 요청 API
 */

class API(private val type: String) {
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
                        result.add(NearbyBusStopParser().parseItemList(parser) as T)
                    }
                    "SearchStopData" -> {
                        result.add(SearchStopParser().parseItemList(parser) as T)
                    }
                    "BusListData" -> {
                        result.add(BusListParser().parseItemList(parser) as T)
                    }
                }
            } else {  // 원하지 않는 태그 스킵
                skip(parser)
            }
        }
        return result
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
