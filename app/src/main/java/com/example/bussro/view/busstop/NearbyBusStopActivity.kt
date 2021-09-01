package com.example.bussro.view.busstop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityNearbyBusStopBinding

/**
 * [NearbyBusStopActivity]
 * MainActivity 의 "내 주변 정류장" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 1km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 */

class NearbyBusStopActivity : AppCompatActivity() {
    private val viewModel by viewModels<NearbyBusStopViewModel>()
    private lateinit var binding: ActivityNearbyBusStopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NearbyBusStopActivity,
            R.layout.activity_nearby_bus_stop
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}