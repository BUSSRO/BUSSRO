package com.example.bussro.view.busstop

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bussro.R
import com.example.bussro.adapter.NearbyBusStopAdapter
import com.example.bussro.databinding.ActivityNearbyBusStopBinding
import com.example.bussro.util.CustomItemDecoration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

/**
 * [NearbyBusStopActivity]
 * MainActivity 의 "내 주변 정류장" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 1km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 */

class NearbyBusStopActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var viewModel: NearbyBusStopViewModel
    private lateinit var binding: ActivityNearbyBusStopBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestLocation: ActivityResultLauncher<Array<String>>
    private lateinit var rvAdapter: NearbyBusStopAdapter
    private lateinit var tts: TextToSpeech
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>

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
                tts.speak(
                    "불러오기 완료",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                )
            })
        }

        // 화면 전환 대응
        if (savedInstanceState == null) {
            viewModel.requestNearbyBusStop()
        }
    }

    /* 변수 초기화 */
    private fun initVar() {
        // Location 객체
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

        // startResultForActivity 객체
        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val stationNm = result.data?.getStringExtra("stationNm")

                    if (!stationNm.isNullOrEmpty()) {
                        viewModel.requestSearchedBusStop(stationNm)
                    }
                }
            }

        // ViewModel 객체
        viewModel =
            ViewModelProvider(this, ViewModelFactory(fusedLocationClient, startActivityForResult))
                .get(NearbyBusStopViewModel::class.java)

        // RecyclerView 세팅
        rvAdapter = NearbyBusStopAdapter(this@NearbyBusStopActivity)
        binding.rvNearbyBusStop.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@NearbyBusStopActivity)
            addItemDecoration(CustomItemDecoration(60))
        }

        // TTS 객체
        tts = TextToSpeech(this, this)
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 언어 설정
            val locale = Locale("ko", "KR")
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show()
            } else {
                // TTS 사용 가능
//                tts.speak("불러오기 완료", TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
            }
        } else {
            Toast.makeText(this, "TTS를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        tts.stop()
        super.onStop()
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}
