package com.example.bussro.feature.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceFragmentCompat
import com.example.bussro.R
import com.example.bussro.databinding.ActivitySettingsBinding

/**
 * [SettingsActivity]
 * MainActivity 의 설정 아이콘 클릭시 보여짐
 * ON/OFF 기능이 주로 제공된다.
 */

class SettingsActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

//        if (savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.frame_settings, SettingsFragment())
//                .commit()
//        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initVar()
    }

    private fun initVar() {
        /* 뒤로가기 */
        binding.ivSettingBack.setOnClickListener {
            finish()
        }

        /* 공지사항 */
        binding.ivSettingNotiDetail.setOnClickListener {

        }

        /* 문의하기 */
        binding.ivSettingInquiryDetail.setOnClickListener {

        }

        /* 개인정보처리방침 */
        binding.ivSettingClDetail.setOnClickListener {

        }

        /* 오픈소스 및 라이센스 */
        binding.ivSettingOsDetail.setOnClickListener {

        }
    }

//    class SettingsFragment : PreferenceFragmentCompat() {
//
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        }
//    }
}