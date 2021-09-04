package com.example.bussro.api

import com.example.bussro.data.BusListData
import com.example.bussro.data.NearbyBusStopData
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
                "arrmsg1" -> arrmsg1 = readArrmsg1(parser)
                "arrmsg2" -> arrmsg2 = readArrmsg2(parser)
                "rtNm" -> rtNm = readRtNm(parser)
                else -> skip(parser)
            }
        }

        return BusListData(arrmsg1, arrmsg2, rtNm)
    }

    /* arrmsg1 태그의 값 리턴 */
    private fun readArrmsg1(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "arrmsg1")
        val arrmsg1 = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "arrmsg1")
        return arrmsg1
    }

    /* arrmsg2 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readArrmsg2(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "arrmsg2")
        val arrmsg2 = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "arrmsg2")
        return arrmsg2
    }

    /* rtNm 태그의 값 리턴 */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRtNm(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "rtNm")
        val rtNm = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "rtNm")
        return rtNm
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