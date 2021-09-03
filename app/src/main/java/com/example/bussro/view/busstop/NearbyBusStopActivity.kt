package com.example.bussro.view.busstop

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bussro.R
import com.example.bussro.api.NearbyBusStopAPI
import com.example.bussro.databinding.ActivityNearbyBusStopBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * [NearbyBusStopActivity]
 * MainActivity 의 "내 주변 정류장" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 1km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 */

class NearbyBusStopActivity : AppCompatActivity() {
    private lateinit var viewModel : NearbyBusStopViewModel
    private lateinit var binding: ActivityNearbyBusStopBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestLocation: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NearbyBusStopActivity,
            R.layout.activity_nearby_bus_stop
        )
        initVar()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        requestPermission()

//        DownLoad().execute()

        // 화면 전환 대응
        if (savedInstanceState == null) {
            viewModel.requestNearbyBusStop()
        }
    }

    /* 변수 초기화 */
    private fun initVar() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        requestLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                it[Manifest.permission.ACCESS_FINE_LOCATION]?.let { granted ->
                    if (granted) {
                        Log.d("test", "위치 권한 설정 완료")
                    }
                    viewModel.requestNearbyBusStop()
                }
            }
        viewModel = ViewModelProvider(this, ViewModelFactory(fusedLocationClient))
            .get(NearbyBusStopViewModel::class.java)
    }

    /* Location 권한 요청 */
    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // FINE + COARSE 둘 다 권한이 없는경우 권한 요청
            requestLocation.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return
        }
    }

//    private inner class DownLoad : AsyncTask<String, Void, String>() {
//        override fun doInBackground(vararg params: String?): String {
//            val urlString = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?tmX=127.0115203&tmY=37.5915668&radius=1000&serviceKey=rrJun7pjU%2FM7Z96bkp784LkI5gvQlPmZfnG%2Bva5Yw0L9ur2e%2FAgvhggKnMotouPPMDC7LGm33dV%2BNCJsvjctjA%3D%3D"
//            Log.d("test", "doInBackground: ${NearbyBusStopAPI().loadXmlFromNetwork(urlString)}")
//            return ""
//        }
//    }
}
