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