package com.example.bussro.feature.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bussro.R

/**
 * [LicenseActivity]
 * SettingsActivity 의 "오픈소스 라이센스" 항목을 선택시 보여짐
 * 버스로 앱 제작시 사용된 오픈소스의 라이센스 목록이 제공된다.
 */

class LicenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
    }
}