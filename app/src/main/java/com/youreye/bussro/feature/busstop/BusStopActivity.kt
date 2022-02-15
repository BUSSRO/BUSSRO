package com.youreye.bussro.feature.busstop

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityBusStopBinding
import com.youreye.bussro.feature.search.SearchActivity
import com.youreye.bussro.util.BussroExceptionHandler
import com.youreye.bussro.util.SharedPrefManager
import com.youreye.bussro.util.logd
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.function.Consumer

/**
 * [BusStopActivity]
 * MainActivity 의 "버스 탑승 도우미" 버튼을 클릭했을시 보여짐
 * 사용자의 위치를 기준으로 0.5km 이내의 버스 정류장을 가까운 순으로 정렬해 제공한다.
 */

@AndroidEntryPoint
class BusStopActivity : AppCompatActivity(), TextToSpeech.OnInitListener, LocationListener {
    private val viewModel: BusStopViewModel by viewModels()
    private lateinit var binding: ActivityBusStopBinding
    private lateinit var tts: TextToSpeech
    private val admitTTS by lazy {
        SharedPrefManager.getTTS(this)
    }
    private lateinit var rvAdapter: BusStopAdapter

    /* 위치 변수 */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var requestingLocationUpdates = false

    /* SearchActivity 에서의 검색값 받기 */
    private val startSearchActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 사용자가 입력한 값
                val station = result.data?.getStringExtra("station")

                station?.apply {
                    logd("검색어 : $station")
                    viewModel.requestSearchedBusStop(this, fusedLocationClient)
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

        requestLocationPermission()
        initVar()
        initObserver()
        initClickListener()
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    /* 위치 지속 업데이트 */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000  // 10초마다 위치 갱신
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /* 객체 초기화 */
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun initVar() {
        // FusedLocationClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // locationCallback 객체 초기화
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location == null) {
                        return
                    }

                    Log.d("TEST", "갱신된 위치 정보 : $location")
                }
            }
        }

        // RecyclerView 초기화
        rvAdapter = BusStopAdapter(application)
        binding.rvNearbyBusStop.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@BusStopActivity)
        }

        binding.rvNearbyBusStop.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter?.itemCount!! - 1

                // 스크롤이 끝에 도달했는지 확인
                if (!binding.rvNearbyBusStop.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount) {
                    rvAdapter.deleteLoading()
                }
            }
        })

        // TTS 객체 초기화
        tts = TextToSpeech(this, this)
    }

    @SuppressLint("MissingPermission")
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
            viewModel.requestBusStop(fusedLocationClient)
        }
    }

    /* Location 권한 요청 */
    private fun requestLocationPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Log.d("TEST", "onPermissionGranted: 위치 권한이 허용되었습니다.")

                // 퍼미션 체크
                if (ActivityCompat.checkSelfPermission(
                        this@BusStopActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@BusStopActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }

                // 현재 위치 가져오기
                viewModel.loadingLiveData.value = true

                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                    .addOnSuccessListener {
                        viewModel.loadingLiveData.value = false

                        if (it != null) {
                            // 사용자 주변 정류장 목록 요청
                            viewModel.requestBusStop(fusedLocationClient)
                        }
                    }
                    .addOnFailureListener {
                        Log.d("TEST", "onPermissionGranted_error: $it")

                        // 위치 불러올 수 없음
                        viewModel.loadFail("location")
                        viewModel.loadingLiveData.value = false
                    }

                // 지속적으로 위치 업데이트 요청
                requestingLocationUpdates = true
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Log.d("TEST", "onPermissionDenied: 위치 권한이 거부되었습니다.")

                Toast.makeText(this@BusStopActivity, "설정에서 권한을 허가 해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // Dialog 띄우기
        TedPermission.create()
            .setPermissionListener(permissionListener)
            .setDeniedMessage("설정 > 권한 에서 권한을 변경할 수 있습니다.")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .check()
    }

    private fun initObserver() {
        // 로딩바
        viewModel.loadingLiveData.observe(this) { isLoading ->
            binding.progressNearbyBusStop.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 버스 정류장
        viewModel.busStopsLiveData.observe(this) { busStopList ->
            rvAdapter.updateData(busStopList)

            if (admitTTS) {
                if (busStopList.isNotEmpty()) {
                    val text =
                        if (viewModel.searchTerm.isNotEmpty()) "${viewModel.searchTerm} 검색 완료" else "불러오기 완료"

                    tts.speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                    )
                } else {
                    val text =
                        if (viewModel.searchTerm.isNotEmpty()) "${viewModel.searchTerm} 검색 실패" else "불러오기 실패"

                    tts.speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                    )
                }
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
        }

        // 데이터 로드 실패 이유
        viewModel.failReason.observe(this) { reason ->
            when (reason) {
                "network" -> {
                    showPlaceHolder(R.drawable.ic_wifi, "네트워크 연결이 없어요")
                }
                "location" -> {
                    showPlaceHolder(R.drawable.ic_error, "위치를 파악할 수 없어요")
                }
                "no_result" -> {
                    showPlaceHolder(R.drawable.ic_error, "주변 정류장이 없어요.\n(서울특별시 지역만 서비스 가능합니다)")
                }
                "no_search_result" -> {
                    showPlaceHolder(R.drawable.ic_search, "검색 결과가 없어요.\n(서울특별시 소재 정류장만 검색 가능합니다)")
                }
            }
        }
    }

    /* PlaceHolder 띄우기 */
    private fun showPlaceHolder(src: Int, text: String) {
        binding.ivNearbyPlaceholderImage.setBackgroundResource(src)
        binding.txtNearbyPlaceholderDesc.text = text

        binding.rvNearbyBusStop.visibility = View.GONE

        binding.ivNearbyPlaceholderImage.visibility = View.VISIBLE
        binding.txtNearbyPlaceholderDesc.visibility = View.VISIBLE
    }

    /* PlaceHolder 숨기기 */
    private fun hidePlaceHolder() {
        binding.ivNearbyPlaceholderImage.visibility = View.GONE
        binding.txtNearbyPlaceholderDesc.visibility = View.GONE

        binding.rvNearbyBusStop.visibility = View.VISIBLE
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

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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

    override fun onLocationChanged(location: Location) {
        Log.d("TEST", "onLocationChanged: $location")
    }
}
