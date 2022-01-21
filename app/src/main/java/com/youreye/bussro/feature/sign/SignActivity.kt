package com.youreye.bussro.feature.sign

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivitySignBinding

/**
 * [SignActivity]
 * 화면에 사용자가 탑승하고 싶은 노선명을 띄움으로써 버스 기사에게 탑승 의사를 밝힘
 */

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private lateinit var rtNm: String
    private var cnt = 0
    private val mDelayHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign)
        // 상태바 색상 변경
        this.window.statusBarColor = resources.getColor(R.color.black)


        // 노선명
        rtNm = intent.getStringExtra("rtNm") ?: ""

        // 전광판
        start()
    }

    private fun start() {
        when (++cnt % 2) {
            0 -> first()
            1 -> second()
        }
    }

    // 배경색 변경
    @SuppressLint("ResourceAsColor")
    private fun first() {
        binding.txtSign.text = rtNm
        binding.txtSign.setTextColor(resources.getColor(R.color.yellow))
        rest()
    }

    // 반짝임
    @SuppressLint("ResourceAsColor")
    private fun second() {
        binding.txtSign.setTextColor(resources.getColor(R.color.black))
        rest()
    }

    private fun rest() {
        mDelayHandler.postDelayed(::start, 700)
    }
}