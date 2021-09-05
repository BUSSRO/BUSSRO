package com.example.bussro.feature.sign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivitySignBinding

/**
 * [SignActivity]
 * 화면에 사용자가 탑승하고 싶은 노선명을 띄움으로써 버스 기사에게 탑승 의사를 밝힘
 */

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign)

        // 노선명
        intent.getStringExtra("rtNm")?.apply {
            binding.txtSign.text = this
        }
    }
}