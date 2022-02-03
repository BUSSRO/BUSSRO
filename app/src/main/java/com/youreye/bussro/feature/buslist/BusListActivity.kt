package com.youreye.bussro.feature.buslist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityBusListBinding
import com.youreye.bussro.feature.dialog.BoardingDialog
import com.youreye.bussro.util.BussroExceptionHandler
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * [BusListActivity]
 * MainActivity 의 "버스 탑승 도우미" 버튼을 클릭했을시 보여짐
 * 사용자가 현재 위치한 버스 정류장을 거치는 버스 리스트를 제공한다.
 */

@AndroidEntryPoint
class BusListActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var viewModel: BusListViewModel
    private lateinit var binding: ActivityBusListBinding
    private lateinit var tts: TextToSpeech
    var rtNm: String = ""  // 사용자가 선택한 버스번호
    private lateinit var rvAdapter: BusListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BussroExceptionHandler.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bus_list)
        initVar()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initSetOnClickListener()
        initTTS()
        initObserver()

        viewModel.requestBusList()
    }

    @SuppressLint("SetTextI18n")
    private fun initVar() {
        // 뒤로가기
        binding.ibBusListBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        // 새로고침
        binding.ivBusListRefresh.setOnClickListener {
            viewModel.requestBusList()
        }

        // TextView 초기화
        val stationNm = intent.getStringExtra("stationNm")!!
        binding.txtBusListLocation.text = "정류장 : $stationNm"

        // ViewModel 객체 초기화
        intent.getStringExtra("arsId")?.apply {
            viewModel = ViewModelProvider(this@BusListActivity, ViewModelFactory(this, stationNm))
                .get(BusListViewModel::class.java)
        }

        rvAdapter = BusListAdapter(this, supportFragmentManager)
        binding.rvBusList.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    private fun initObserver() {
        // 로딩바
        viewModel.loadingLiveData.observe(this) { isLoading ->
            binding.progressBusList.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 버스 정류장
        viewModel.busListLiveData.observe(this) { busStopList ->
            rvAdapter.updateData(busStopList)

            if (busStopList.isNotEmpty()) {
                tts.speak(
                    "불러오기 완료",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                )
            } else {
                tts.speak(
                    "불러오기 실패",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                )
            }

            /* PlaceHolder 숨기기 */
            if (binding.ivBusListPlaceholderImage.isVisible) {
                hidePlaceHolder()
            }
        }

        // 데이터 로드 실패 이유
        viewModel.failReason.observe(this) { reason ->
            when (reason) {
                "network" -> {
                    showPlaceHolder(R.drawable.ic_wifi, "네트워크 연결이 없어요")
                }
                "no_result" -> {
                    showPlaceHolder(
                        R.drawable.ic_error,
                        "주변 정류장이 없어요.\n(서울특별시 지역만 서비스 가능합니다)"
                    )
                }
            }
        }
    }

    /* PlaceHolder 띄우기 */
    private fun showPlaceHolder(src: Int, text: String) {
        binding.ivBusListPlaceholderImage.setBackgroundResource(src)
        binding.txtBusListPlaceholderDesc.text = text

        binding.ivBusListPlaceholderImage.visibility = View.VISIBLE
        binding.txtBusListPlaceholderDesc.visibility = View.VISIBLE
    }

    /* PlaceHolder 숨기기 */
    private fun hidePlaceHolder() {
        binding.ivBusListPlaceholderImage.visibility = View.GONE
        binding.txtBusListPlaceholderDesc.visibility = View.GONE
    }

    private fun initTTS() {
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 언어 설정
            val locale = Locale("ko", "KR")
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this@BusListActivity, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show()
            } else {
//                viewModel.requestBusList()
                // TTS 사용 가능
                // ERROR: 21-09-04 TTS 중첩 오류 발생으로 주석처리
//                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
//                    override fun onStart(utteranceId: String?) {
//                    }
//
//                    override fun onDone(utteranceId: String?) {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            whenTTSdone()
//                        }
//                    }
//
//                    override fun onError(utteranceId: String?) {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            whenTTSdone()
//                        }
//                    }
//                })
//                tts.speak("원하는 버스 정보를 선택하면 도착 정보를 드릴게요.", TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
            }
        } else {
            Toast.makeText(this@BusListActivity, "TTS를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    private fun whenTTSdone() {
        binding.apply {
            // TTS 관련된 view 없애기
            txtBusList.visibility = View.GONE
            imgBusList.visibility = View.GONE
            viewBusListDimmed.visibility = View.GONE

            // background view 선택 가능하도록
        }
    }

     */

    private fun initSetOnClickListener() {
        /* 전광판 or 카메라 인식 */
        binding.txtBusListStart.setOnClickListener {
            val dialog = BoardingDialog(
                rtNm,
                intent.getStringExtra("stationNm")!!,
                intent.getStringExtra("arsId")!!
            )
            dialog.show(supportFragmentManager, "FromBusListActivity")
        }
    }

    /* [302번 버스로 도착을 안내합니다.] 버튼 띄워주기 */
    @SuppressLint("SetTextI18n")
    fun setVisible() {
        if (rtNm.isNotEmpty()) {
            binding.txtBusListStart.text = "${rtNm}번 버스로 도착을 안내합니다."
            binding.txtBusListStart.visibility = View.VISIBLE
        } else {
            binding.txtBusListStart.visibility = View.GONE
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
//        if (tts.isSpeaking) {
//            tts.stop()
//            // whenTTSdone()
//        } else {
//            super.onBackPressed()
//        }

        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}