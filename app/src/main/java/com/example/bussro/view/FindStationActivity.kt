package com.example.bussro.view

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
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityFindStationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

/**
 * [FindStationActivity]
 * MainActivity 의 "내 주변 정류장" 버튼을 클릭했을시 보여짐
 * TTS & STT 를 이용해서 사용자가 찾는 정류장을 입력받는다.
 *
 * @author 윤주연(otu165)
 */

class FindStationActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityFindStationBinding
    private lateinit var tts: TextToSpeech  // TTS 객체
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var destination: String

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
                        destination = data[0]
                        binding.txtFindStation.text = "$destination 정류장을 검색합니다."

                        tts.speak("$destination 정류장을 검색합니다.", TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
                        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                            }

                            override fun onDone(utteranceId: String?) {
                                val intent = Intent(this@FindStationActivity, BusStationActivity::class.java)
                                    .putExtra("station", destination)
                                startActivity(intent)
                            }

                            override fun onError(utteranceId: String?) {
                            }
                        })
                    }
                } else {
                    Toast.makeText(this@FindStationActivity, "이미지를 눌러 음성인식을 재실행할 수 있습니다.", Toast.LENGTH_SHORT).show()
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
                    }

                    override fun onDone(utteranceId: String?) {
                        requestSTT()
                    }

                    override fun onError(utteranceId: String?) {
                    }
                })

                tts.speak("찾는 정류장을 말해주세요.", TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
            }
        } else {
            Toast.makeText(this@FindStationActivity, "TTS를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSTT() {
        CoroutineScope(Dispatchers.Main).launch {
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