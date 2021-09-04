package com.example.bussro.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bussro.adapter.BusListAdapter
import com.example.bussro.data.BusListRvData
import com.example.bussro.util.CustomItemDecoration
import com.example.bussro.R
import com.example.bussro.databinding.ActivityBusListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * [BusListActivity]
 * MainActivity 의 "버스 탑승 도우미" 버튼을 클릭했을시 보여짐
 * 사용자가 현재 위치한 버스 정류장을 거치는 버스 리스트를 제공한다.
 */

class BusListActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityBusListBinding
    private lateinit var tts: TextToSpeech
    private val busList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bus_list)

        if (intent.hasExtra("station")) {
            Log.d("test","사용자가 선택한 버스 정류장 : ${intent.getStringExtra("station")}")
            binding.txtBusListLocation.text = intent.getStringExtra("station")
        }

        initBusListRv()
        initSetOnClickListener()
        initTTS()
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

    private fun initBusListRv() {
        val rvAdapter = BusListAdapter(applicationContext, getTempBusList(), busList, this)
        binding.rvBusList.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(
                CustomItemDecoration(60)
            )
        }

        rvAdapter.notifyDataSetChanged()
    }

    private fun initSetOnClickListener() {
        binding.txtBusListStart.setOnClickListener {
            val intent = Intent(this, BusInfoActivity::class.java)
                .putExtras(bundleOf("busList" to busList))
            startActivity(intent)
        }
    }

    // TODO? 임시 데이터 제거
    private fun getTempBusList() : MutableList<BusListRvData> {
        val tempData = mutableListOf<BusListRvData>()

        for (i in 1..10) {
            tempData.add(BusListRvData(i.toString()))
        }

        return tempData
    }

    fun setVisible() {
        if (busList.size > 0) {
            binding.txtBusListStart.visibility = View.VISIBLE

        } else {
            binding.txtBusListStart.visibility = View.GONE
        }
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