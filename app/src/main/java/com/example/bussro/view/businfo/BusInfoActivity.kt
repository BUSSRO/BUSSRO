package com.example.bussro.view.businfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityBusInfoBinding

/**
 * [BusInfoActivity]
 * BusListActivity 의 "이 버스로 도착을 안내하겠습니다." 버튼을 클릭했을시 보여짐
 * 사용자가 선택한 버스 번호의 위치 정보가 변경될때마다 STT 로 안내한다.
 */

class BusInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBusInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bus_info)

        // set Location
        intent.getStringExtra("stationNm")?.apply {
            Log.d("test", "onCreate: $this")
            binding.txtBusListLocation.text = this
        }
    }
}