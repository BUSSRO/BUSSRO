package com.example.bussro.api

import com.example.bussro.data.SearchStopData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.IllegalStateException

/**
 * [SearchStopParser]
 * 서울특별시_정류소정보조회서비스 中 정류소 명칭 검색 response message parser
 */

class SearchStopParser {
    private val ns: String? = null

    /* API 의 Response Message 중 itemList 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parseItemList(parser: XmlPullParser) : SearchStopData {
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
                "stNm" -> stNm = readStationNm(parser)
                "tmX" -> tmX = readTmX(parser)
                "tmY" -> tmY = readTmY(parser)
                "arsId" -> arsId = readArsId(parser)
                else -> skip(parser)
            }
        }

        return SearchStopData(stNm, tmX, tmY, arsId)
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

    /* arsId 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readArsId(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "arsId")
        val arsId = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "arsId")
        return arsId
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