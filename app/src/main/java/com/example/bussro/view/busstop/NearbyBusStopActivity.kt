package com.example.bussro.view.busstop

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bussro.R
import com.example.bussro.adapter.BusListAdapter
import com.example.bussro.adapter.NearbyBusStopAdapter
import com.example.bussro.api.NearbyBusStopAPI
import com.example.bussro.databinding.ActivityNearbyBusStopBinding
import com.example.bussro.util.CustomItemDecoration
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
    private lateinit var rvAdapter: NearbyBusStopAdapter

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

        // LiveData 관찰
        viewModel.apply {
            // loading 데이터 변경 감지
            loadingLiveData.observe(this@NearbyBusStopActivity, Observer { flag ->
                binding.progressNearbyBusStop.visibility = if (flag) View.VISIBLE else View.GONE
            })
            // busStop 데이터 변경 감지
            busStopsLiveData.observe(this@NearbyBusStopActivity, Observer { data ->
                rvAdapter.updateData(data)
            })
        }

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

        // RecyclerView 세팅
        rvAdapter = NearbyBusStopAdapter(this@NearbyBusStopActivity)
        binding.rvNearbyBusStop.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@NearbyBusStopActivity)
            addItemDecoration(CustomItemDecoration(60))
        }
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
}
