package com.example.bussro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.bussro.R
import kotlin.math.log

class BusInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_info)

        val busList = intent.extras?.get("busList")
        Log.d("TEST", "${busList.toString()}")
    }
}