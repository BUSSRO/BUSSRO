package com.example.bussro.api

import android.util.Xml
import com.example.bussro.data.TestData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException

/**
 * 서울특별시_정류소정보조회 서비스 中 getStationByNameList(정류소 명칭 검색) 응답 파서
 */

class TestParser {
    private val ns: String? = null

    /* API 의 Response Message 를 파싱한 결과를 리턴함 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<TestData> {
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
    private fun parseServiceResult(parser: XmlPullParser) : List<TestData> {
        val testData = mutableListOf<TestData>()
        parser.require(XmlPullParser.START_TAG, ns, "ServiceResult")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "msgBody") {
                parseMsgBody(parser).let { data ->
                    testData.addAll(data)
                }
            }
            else {  // 원하지 않는 태그(comMsgHeader, msgHeader) 스킵
                skip(parser)
            }
        }
        return testData
    }

    /* API 의 Response Message 중 msgBody 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMsgBody(parser: XmlPullParser) : List<TestData> {
        val result = mutableListOf<TestData>()
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
    private fun parseItemList(parser: XmlPullParser) : TestData {
        var stNm: String? = null
        var tmX: Double? = null
        var tmY: Double? = null


        parser.require(XmlPullParser.START_TAG, ns, "itemList")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "stNm" -> stNm = readStationNm(parser)
                "tmX" -> tmX = readTmX(parser)
                "tmY" -> tmY = readTmY(parser)
                else -> skip(parser)
            }
        }

        return TestData(stNm, tmX, tmY)
    }

    /* stNm 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readStationNm(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "stNm")
        val stNm = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "stNm")
        return stNm
    }

    /* tmX 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTmX(parser: XmlPullParser): Double {
        parser.require(XmlPullParser.START_TAG, ns, "tmX")
        val tmX = readText(parser).toDouble()
        parser.require(XmlPullParser.END_TAG, ns, "tmX")
        return tmX
    }

    /* tmY 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTmY(parser: XmlPullParser): Double {
        parser.require(XmlPullParser.START_TAG, ns, "tmY")
        val tmY = readText(parser).toDouble()
        parser.require(XmlPullParser.END_TAG, ns, "tmY")
        return tmY
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