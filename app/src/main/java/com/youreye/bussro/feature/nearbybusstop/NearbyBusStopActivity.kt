package com.youreye.bussro.feature.nearbybusstop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityNearbyBusStopBinding
import com.youreye.bussro.feature.findstation.FindStationActivity
import com.youreye.bussro.model.repository.HistoryRepository
import com.youreye.bussro.util.CustomItemDecoration
import com.youreye.bussro.util.NetworkConnection
import com.youreye.bussro.util.logd
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

/**
 * [NearbyBusStopActivity]
 * MainActivity 의 "버스 탑승 도우미" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 0.4km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 */

@AndroidEntryPoint
class NearbyBusStopActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val viewModel: NearbyBusStopViewModel by viewModels()
    private lateinit var binding: ActivityNearbyBusStopBinding
    private lateinit var requestLocation: ActivityResultLauncher<Array<String>>
    private lateinit var tts: TextToSpeech
    @Inject lateinit var connection: NetworkConnection

    // 음성으로 검색하는 것
    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val stationNm = result.data?.getStringExtra("stationNm")
                if (!stationNm.isNullOrEmpty()) {
                    viewModel.requestSearchedBusStop(stationNm)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NearbyBusStopActivity,
            R.layout.activity_nearby_bus_stop
        )
        binding.lifecycleOwner = this

        // 네트워크 연결 확인
        connection.observe(this, Observer { isConnected ->
            if (isConnected) {
                binding.ivNearbyPlaceholderImage.visibility = View.GONE
                binding.txtNearbyPlaceholderDesc.visibility = View.GONE

                // 데이터 요청
                viewModel.requestNearbyBusStop()

                binding.rvNearbyBusStop.visibility = View.VISIBLE
                binding.edtNearbyBusStop.visibility = View.VISIBLE
                binding.imgNearbyBusStop.visibility = View.VISIBLE
            } else {
                binding.rvNearbyBusStop.visibility = View.GONE
                binding.edtNearbyBusStop.visibility = View.GONE
                binding.imgNearbyBusStop.visibility = View.GONE

                binding.ivNearbyPlaceholderImage.setBackgroundResource(R.drawable.ic_baseline_wifi_off_24)
                binding.txtNearbyPlaceholderDesc.text = "네트워크 연결 없음"

                binding.ivNearbyPlaceholderImage.visibility = View.VISIBLE
                binding.txtNearbyPlaceholderDesc.visibility = View.VISIBLE
            }
        })

        initVar()
        binding.viewModel = viewModel
        binding.activity = this@NearbyBusStopActivity
        requestPermission()
    }

    /* onClick */
    fun onclick(view: View) {
        val intent = Intent(view.context, FindStationActivity::class.java)
        startActivityForResult.launch(intent)
    }

    /* 변수 초기화 */
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun initVar() {
        // Location 객체
        requestLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                it[Manifest.permission.ACCESS_FINE_LOCATION]?.let { granted ->
                    if (granted) {
                        logd("위치 권한 설정 완료")
                    }
                    // 잠깐 뺐음
//                    viewModel.requestNearbyBusStop()
                }
            }

        // RecyclerView 세팅
        val rvAdapter = NearbyBusStopAdapter(application)
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

                /* 버스정류장이 없는 경우 */
                if (data.isEmpty()) {
                    binding.ivNearbyPlaceholderImage.setBackgroundResource(R.drawable.ic_search_off)
                    binding.txtNearbyPlaceholderDesc.text = "해당하는 정류장이 없어요.\n(서울특별시 소재 정류장만 서비스 가능합니다)"

                    binding.ivNearbyPlaceholderImage.visibility = View.VISIBLE
                    binding.txtNearbyPlaceholderDesc.visibility = View.VISIBLE
                } else {
                    binding.ivNearbyPlaceholderImage.visibility = View.GONE
                    binding.txtNearbyPlaceholderDesc.visibility = View.GONE
                }
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
