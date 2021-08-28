package com.example.bussro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.bussro.R

/**
 * [BusStationActivity]
 * FindStationActivity 의 STT 기능 완료 후 보여짐
 *
 * @author 윤주연(otu165)
 */

class BusStationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_station)

        if (intent.hasExtra("station")) {
            findViewById<TextView>(R.id.txt_test).text = "${intent.getStringExtra("station")} 정류장"
        }
    }
}