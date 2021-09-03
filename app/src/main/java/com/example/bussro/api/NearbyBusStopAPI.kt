package com.example.bussro.api

import com.example.bussro.data.NearbyBusStops
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * [NearbyBusStopAPI]
 * 공공데이터 OpenAPI (서울특별시_정류소정보조회 서비스)
 */

class NearbyBusStopAPI {
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

    /* API 의 Response Message 가져오기 */
    @Throws(XmlPullParserException::class, IOException::class)
    fun loadXmlFromNetwork(urlString: String): List<NearbyBusStops> {
        return downloadUrl(urlString)?.use { stream ->
            NearbyBusStopParser().parse(stream)
        } ?: emptyList()
    }
}