package com.youreye.bussro.feature.busstop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityBusStopBinding
import com.youreye.bussro.feature.search.SearchActivity
import com.youreye.bussro.util.BussroExceptionHandler
import com.youreye.bussro.util.logd
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * [BusStopActivity]
 * MainActivity 의 "버스 탑승 도우미" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 0.4km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 */

@AndroidEntryPoint
class BusStopActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val viewModel: BusStopViewModel by viewModels()
    private lateinit var binding: ActivityBusStopBinding
    private lateinit var requestLocation: ActivityResultLauncher<Array<String>>
    private lateinit var tts: TextToSpeech
    private lateinit var rvAdapter: BusStopAdapter

    /* SearchActivity 에서의 검색값 받기 */
    private val startSearchActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 사용자가 입력한 값
                val station = result.data?.getStringExtra("station")

                station?.apply {
                    logd("검색어 : $station")
                    viewModel.requestSearchedBusStop(this)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BussroExceptionHandler.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bus_stop)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.activity = this@BusStopActivity
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initVar()
        initClickListener()
        requestLocationPermission()
        initObserver()

        // 사용자 주변 정류장 목록 요청
        viewModel.requestBusStop()
    }

    /* 객체 초기화 */
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun initVar() {
        // Location 객체
        requestLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                it[Manifest.permission.ACCESS_FINE_LOCATION]?.let { granted ->
                    if (granted) {
                        // 사용자 주변 정류장 목록 요청
                        viewModel.requestBusStop()
                    }
                }
            }

        // RecyclerView 세팅
        rvAdapter = BusStopAdapter(application)
        binding.rvNearbyBusStop.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@BusStopActivity)
        }

        binding.rvNearbyBusStop.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter?.itemCount!! - 1

                // 스크롤이 끝에 도달했는지 확인
                if (!binding.rvNearbyBusStop.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    rvAdapter.deleteLoading()
                }
            }
        })

        // TTS 객체
        tts = TextToSpeech(this, this)
    }

    private fun requestMovie() {
        TODO("Not yet implemented")
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.ibSearchBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        /* 검색 */
        binding.txtBusStopSearch.setOnClickListener {
            startSearchActivityForResult.launch(Intent(this, SearchActivity::class.java))
        }

        /* 새로고침 */
        binding.ivBusStopRefresh.setOnClickListener {
            viewModel.requestBusStop()
        }
    }

    /* Location 권한 요청 */
    private fun requestLocationPermission() {
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

    private fun initObserver() {
        // 로딩바
        viewModel.loadingLiveData.observe(this, { isLoading ->
            binding.progressNearbyBusStop.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // 버스 정류장
        viewModel.busStopsLiveData.observe(this, { busStopList ->
            rvAdapter.updateData(busStopList)

            if (busStopList.isNotEmpty()) {
                val text = if (viewModel.searchTerm.isNotEmpty()) "${viewModel.searchTerm} 검색 완료" else "불러오기 완료"

                tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                )
            } else {
                val text = if (viewModel.searchTerm.isNotEmpty()) "${viewModel.searchTerm} 검색 실패" else "불러오기 실패"

                tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                )
            }

            /* 안내문구 갱신 */
            val text = "${busStopList.size}개의 정류장이 나왔습니다."
            val builder = SpannableStringBuilder(text)
            val colorSpan = ForegroundColorSpan(resources.getColor(R.color.yellow))
            builder.setSpan(
                colorSpan,
                0,
                busStopList.size.toString().length + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.txtBusStopDesc.text = builder

            /* PlaceHolder 숨기기 */
            if (binding.ivNearbyPlaceholderImage.isVisible) {
                hidePlaceHolder()
            }
        })

        // 데이터 로드 실패 이유
        viewModel.failReason.observe(this, { reason ->
            when(reason) {
                "network" -> {
                    showPlaceHolder(R.drawable.ic_baseline_wifi_off_24, "네트워크 연결이 없어요")
                }
                "location" -> {
                    showPlaceHolder(R.drawable.ic_baseline_warning_24, "위치를 파악할 수 없어요")
                }
                "no_result" -> {
                    showPlaceHolder(R.drawable.ic_baseline_warning_24, "주변 정류장이 없어요.\n(서울특별시 지역만 서비스 가능합니다)")
                }
                "no_search_result" -> {
                    showPlaceHolder(R.drawable.ic_search_off, "검색 결과가 없어요.\n(서울특별시 소재 정류장만 검색 가능합니다)")
                }
            }
        })
    }

    /* PlaceHolder 띄우기 */
    private fun showPlaceHolder(src: Int, text: String) {
        binding.ivNearbyPlaceholderImage.setBackgroundResource(src)
        binding.txtNearbyPlaceholderDesc.text = text

        binding.ivNearbyPlaceholderImage.visibility = View.VISIBLE
        binding.txtNearbyPlaceholderDesc.visibility = View.VISIBLE
    }

    /* PlaceHolder 숨기기 */
    private fun hidePlaceHolder() {
        binding.ivNearbyPlaceholderImage.visibility = View.GONE
        binding.txtNearbyPlaceholderDesc.visibility = View.GONE
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}
