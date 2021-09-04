package com.example.bussro.view.findstation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityFindStationBinding
import com.example.bussro.view.BusListActivity
import java.util.*

/**
 * [FindStationActivity]
 * NearbyBusStopActivity 의 "다른 정류장 찾기" 버튼 클릭시 보여짐
 * TTS & STT 를 이용해서 사용자가 찾는 정류장을 입력받는다.
 */

class FindStationActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityFindStationBinding
    private lateinit var tts: TextToSpeech  // TTS 객체
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_station)

        initVar()
        initSetOnClickListener()
    }

    /* 객체 초기화 */
    private fun initVar() {
        // 1. TextToSpeech 객체
        tts = TextToSpeech(this@FindStationActivity, this@FindStationActivity)

        // 2. ActivityResultLauncher 객체
        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    if (!data.isNullOrEmpty()) {
                        // 음성인식 결과 확인
                        val destination = data[0].toString()
                        binding.txtFindStation.text = "$destination 정류장을 검색합니다."

                        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                binding.imgFindStation.isClickable = false
                            }

                            override fun onDone(utteranceId: String?) {
                                // 버스 도착정보 안내 액티비티로 이동
//                                val intent = Intent(this@FindStationActivity, BusListActivity::class.java)
//                                    .putExtra("station", destination)
//                                startActivity(intent)

                                // CHECK: NearbyBusStopActivity 로 데이터 전달
                                setResult(Activity.RESULT_OK, Intent().putExtra("stationNm", destination))
                                finish()
                            }

                            override fun onError(utteranceId: String?) {
                            }
                        })

                        Handler(Looper.getMainLooper()).postDelayed({
                            tts.speak("$destination 정류장을 검색합니다.", TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
                        }, 500)
                    }
                } else {
                    val toast = Toast.makeText(this@FindStationActivity, "이미지를 눌러 음성인식을 재실행할 수 있습니다.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
                    toast.show()
                }
            }
    }

    private fun initSetOnClickListener() {
        binding.imgFindStation.setOnClickListener {
            requestSTT()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 언어 설정
            val locale = Locale("ko", "KR")
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this@FindStationActivity, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show()
            } else {
                // TTS 사용 가능
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        binding.imgFindStation.isClickable = false
                    }

                    override fun onDone(utteranceId: String?) {
                        requestSTT()
                        binding.imgFindStation.isClickable = true
                    }

                    override fun onError(utteranceId: String?) {
                        binding.imgFindStation.isClickable = true

                        val toast = Toast.makeText(this@FindStationActivity, "이미지를 눌러 음성인식을 재실행할 수 있습니다.", Toast.LENGTH_SHORT)
//                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
                        toast.show()
                    }
                })

                tts.speak("찾는 정류장을 말해주세요.", TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
            }
        } else {
            Toast.makeText(this@FindStationActivity, "TTS를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSTT() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "찾는 정류장을 말해주세요.")  // 예시로 보여지는 텍스트
            }

            startActivityForResult.launch(intent)

        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this@FindStationActivity, "STT를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
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
        }

        super.onBackPressed()
    }
}