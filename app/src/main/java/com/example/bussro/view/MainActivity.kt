package com.example.bussro.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityMainBinding

/**
 * [MainActivity]
 * SplashActivity 후 보여짐
 * 사용자는 원하는 버튼을 선택해서 원하는 기능으로 이동할 수 있다.
 *
 * @author 윤주연(otu165)
 */

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initSetOnClickListener()
    }

    private fun initSetOnClickListener() {
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
            val toast = Toast.makeText(this@MainActivity, "아직 구현되지 않은 기능입니다.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }
}