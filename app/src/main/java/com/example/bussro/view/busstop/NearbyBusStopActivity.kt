package com.example.bussro.view.busstop

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityNearbyBusStopBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * [NearbyBusStopActivity]
 * MainActivity 의 "내 주변 정류장" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 1km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 */

class NearbyBusStopActivity : AppCompatActivity() {
//    private val viewModel by viewModels<NearbyBusStopViewModel>()
    private lateinit var binding: ActivityNearbyBusStopBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestLocation =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it[Manifest.permission.ACCESS_FINE_LOCATION]?.let { granted ->
                if (granted) {
                    Log.d("test", "위치 권한 설정 완료")
                }
                // TODO: 위치 정보 요청하기
                requestNearbyBusStop()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NearbyBusStopActivity,
            R.layout.activity_nearby_bus_stop
        )
        binding.lifecycleOwner = this
//        binding.viewModel = viewModel
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        // request permission for location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // FINE + COARSE 둘 다 권한이 없는경우 권한 요청
            requestLocation.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))

            return
        }

        if (savedInstanceState == null) {
            requestNearbyBusStop()
        }
    }

    /* 사용자 주변 버스 정류장 요청 메서드 */
    @SuppressLint("MissingPermission")
    fun requestNearbyBusStop() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("test", "위도 : ${location.latitude}, 경도 : ${location.longitude}")

                    CoroutineScope(Dispatchers.IO).launch {
                        // TODO: 서버 데이터 받아오기
                    }
                }
            }
            .addOnFailureListener {  // 위치 정보 받아올 수 없음
                Log.d("test", "requestNearbyBusStop: 위치 정보 받아올 수 없음")
            }
    }
}
