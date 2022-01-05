package com.example.bussro.feature.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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
            val intent = Intent(this, WebViewActivity::class.java)
                .putExtra("addr", NOTIFICATION_PAGE)
            startActivity(intent)
        }

        /* 문의하기 */
        binding.ivSettingInquiryDetail.setOnClickListener {

        }

        /* 개인정보처리방침 */
        binding.ivSettingClDetail.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
                .putExtra("addr", PERSONAL_PAGE)
            startActivity(intent)
        }

        /* 오픈소스 및 라이센스 */
        binding.ivSettingOsDetail.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
                .putExtra("addr", LICENSE_PAGE)
            startActivity(intent)
        }
    }

    companion object {
        private const val NOTIFICATION_PAGE = "https://stitch-mandarin-baa.notion.site/70c0a410a7c6473b839962d06b107e59"
        private const val PERSONAL_PAGE = "https://stitch-mandarin-baa.notion.site/bb0dd13476d64c01b820c673bac42602"
        private const val LICENSE_PAGE = "https://stitch-mandarin-baa.notion.site/daee86be24c14b3d8f50c66aafc753a1"
    }

//    class SettingsFragment : PreferenceFragmentCompat() {
//
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        }
//    }
}