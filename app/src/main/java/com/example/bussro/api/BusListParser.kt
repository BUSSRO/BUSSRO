package com.example.bussro.api

import com.example.bussro.data.BusListData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.IllegalStateException

class BusListParser {
    private val ns: String? = null

    /* API 의 Response Message 중 itemList 태그를 파싱 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parseItemList(parser: XmlPullParser) : BusListData {
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