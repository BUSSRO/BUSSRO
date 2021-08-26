package com.example.bussro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.bussro.R
import kotlin.math.log

/**
 * [BusInfoActivity]
 * BusListActivity 의 "이 버스로 도착을 안내하겠습니다." 버튼을 클릭했을시 보여짐
 * 사용자가 선택한 버스 번호의 위치 정보가 변경될때마다 STT 로 안내한다.
 *
 * @author 윤주연(otu165)
 */

class BusInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_info)

        val busList = intent.extras?.get("busList")
        Log.d("TEST", "${busList.toString()}")
    }
}