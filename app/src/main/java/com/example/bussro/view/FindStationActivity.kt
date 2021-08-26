package com.example.bussro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityFindStationBinding
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
    private lateinit var tts: TextToSpeech
    private var ready = false  // tts 사용 가능 여부

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_station)
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("ko", "KR")  // 언어(한국어), 국가(대한민국)
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // TODO? 토스트에서 더 사용자 친화적인 방법으로 변경하기
                Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show()
            } else {
                ready = true
                tts.speak("찾는 정류장을 말해주세요.", TextToSpeech.QUEUE_FLUSH, null, null)
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