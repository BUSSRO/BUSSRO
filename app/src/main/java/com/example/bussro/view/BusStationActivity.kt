package com.example.bussro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.bussro.R

class BusStationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_station)

        if (intent.hasExtra("station")) {
            findViewById<TextView>(R.id.txt_test).text = intent.getStringExtra("station")
        }
    }
}