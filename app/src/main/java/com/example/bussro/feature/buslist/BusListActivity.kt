package com.example.bussro.feature.buslist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bussro.util.CustomItemDecoration
import com.example.bussro.R
import com.example.bussro.databinding.ActivityBusListBinding
import com.example.bussro.feature.sign.SignActivity
import com.example.bussro.util.logd
import java.util.*

/**
 * [BusListActivity]
 * MainActivity 의 "버스 탑승 도우미" 버튼을 클릭했을시 보여짐
 * 사용자가 현재 위치한 버스 정류장을 거치는 버스 리스트를 제공한다.
 */

class BusListActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var viewModel: BusListViewModel
    private lateinit var binding: ActivityBusListBinding
    private lateinit var tts: TextToSpeech
    private val busList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bus_list)
        initVar()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initSetOnClickListener()
        initTTS()

        if (savedInstanceState == null) {
            viewModel.requestBusList()
        }
    }

    private fun initVar() {
        // TextView 초기화
        val stationNm = intent.getStringExtra("stationNm")!!
        binding.txtBusListLocation.text = stationNm

        // ViewModel 객체 초기화
        intent.getStringExtra("arsId")?.apply {
            viewModel = ViewModelProvider(this@BusListActivity, ViewModelFactory(this, stationNm))
                .get(BusListViewModel::class.java)
            logd("정류장고유번호 : arsId: ${intent.getStringExtra("arsId")}")
        }

        val rvAdapter = BusListAdapter(busList, this)
        binding.rvBusList.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(
                CustomItemDecoration(60)
            )
        }

        // LiveData 관찰
        viewModel.apply {
            busListLiveData.observe(this@BusListActivity, { data ->
                rvAdapter.updateData(data)
                tts.speak(
                    "불러오기 완료",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                )
            })
            loadingLiveData.observe(this@BusListActivity, { flag ->
                binding.progressBusList.visibility = if (flag) View.VISIBLE else View.GONE
            })
        }
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

    private fun whenTTSdone() {
        binding.apply {
            // TTS 관련된 view 없애기
            txtBusList.visibility = View.GONE
            imgBusList.visibility = View.GONE
            viewBusListDimmed.visibility = View.GONE

            // background view 선택 가능하도록
        }
    }

    private fun initSetOnClickListener() {
        binding.txtBusListStart.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
                .putExtras(bundleOf("busList" to busList))
                .putExtra("rtNm", busList[0])
            startActivity(intent)
        }
    }

    fun setVisible() {
        if (busList.size > 0) {
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
        if (tts.isSpeaking) {
            tts.stop()
            whenTTSdone()
        } else {
            super.onBackPressed()
        }
    }
}