package com.example.bussro.feature.nearbybusstop

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bussro.R
import com.example.bussro.databinding.ActivityNearbyBusStopBinding
import com.example.bussro.feature.findstation.FindStationActivity
import com.example.bussro.model.repository.HistoryRepository
import com.example.bussro.util.CustomItemDecoration
import com.example.bussro.util.logd
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

/**
 * [NearbyBusStopActivity]
 * MainActivity 의 "내 주변 정류장" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 1km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 *
 * TODO: 무선인터넷 연결 여부 확인 후 예외 처리하기
 */

@AndroidEntryPoint
class NearbyBusStopActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val viewModel: NearbyBusStopViewModel by viewModels()
    private lateinit var binding: ActivityNearbyBusStopBinding
    private lateinit var requestLocation: ActivityResultLauncher<Array<String>>
    private lateinit var tts: TextToSpeech
    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val stationNm = result.data?.getStringExtra("stationNm")

                if (!stationNm.isNullOrEmpty()) {
                    viewModel.requestSearchedBusStop(stationNm)
                }
            }
        }
    @Inject lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NearbyBusStopActivity,
            R.layout.activity_nearby_bus_stop
        )
        initVar()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.activity = this@NearbyBusStopActivity
        requestPermission()

        // 화면 전환 대응
        if (savedInstanceState == null) {
            viewModel.requestNearbyBusStop()
        }
    }

    /* onClick */
    fun onclick(view: View) {
        val intent = Intent(view.context, FindStationActivity::class.java)
        startActivityForResult.launch(intent)
    }

    /* 변수 초기화 */
    private fun initVar() {
        // Location 객체
        requestLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                it[Manifest.permission.ACCESS_FINE_LOCATION]?.let { granted ->
                    if (granted) {
                        logd("위치 권한 설정 완료")
                    }
                    viewModel.requestNearbyBusStop()
                }
            }

        // RecyclerView 세팅
        val rvAdapter = NearbyBusStopAdapter(historyRepository, application)
        binding.rvNearbyBusStop.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@NearbyBusStopActivity)
            addItemDecoration(CustomItemDecoration(60))
        }

        // TTS 객체
        tts = TextToSpeech(this, this)

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

        // EditText Search 감지
        binding.edtNearbyBusStop.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.requestSearchedBusStop(v.text.toString())

                // 키보드 동작
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtNearbyBusStop.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 언어 설정
            val locale = Locale("ko", "KR")
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show()
            } else {
                // TTS 사용 가능
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
