package com.youreye.bussro.feature.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivitySettingsBinding
import com.youreye.bussro.util.ErrorHandlerManager

/**
 * [SettingsActivity]
 * MainActivity 의 설정 아이콘 클릭시 보여짐
 * ON/OFF 기능이 주로 제공된다.
 */

class SettingsActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ErrorHandlerManager.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

//        if (savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.frame_settings, SettingsFragment())
//                .commit()
//        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initVar()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun initVar() {
        /* 뒤로가기 */
        binding.ivSettingBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        /* 공지사항 */
        binding.ivSettingNotiDetail.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
                .putExtra("addr", NOTIFICATION_PAGE)
            startActivity(intent)
        }

        /* 문의하기 (이메일로 연결) */
        binding.ivSettingInquiryDetail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${getString(R.string.email)}")
            intent.putExtra(Intent.EXTRA_SUBJECT, "[버스스로] 사용자 건의사항")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
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