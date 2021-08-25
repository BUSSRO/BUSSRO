package com.example.bussro.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        // 1. 내 주변 정류장
        binding.txtMainFirst.setOnClickListener {
            val intent = Intent(this, FindStationActivity::class.java)
            startActivity(intent)
        }

        // 2. 버스 탑승 도우미
        binding.txtMainSecond.setOnClickListener {
            val intent = Intent(this, BusListActivity::class.java)
            startActivity(intent)
        }

        // 3. 하차벨 위치
        binding.txtMainThird.setOnClickListener {
            // TODO
        }
    }
}