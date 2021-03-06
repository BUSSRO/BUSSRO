package com.youreye.bussro.feature.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.BuildConfig
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivitySettingsBinding
import com.youreye.bussro.feature.dialog.SuggestionsDialog
import com.youreye.bussro.util.BussroExceptionHandler
import com.youreye.bussro.util.SharedPrefManager

/**
 * [SettingsActivity]
 * MainActivity 의 설정 아이콘 클릭시 보여짐
 * ON/OFF 기능이 주로 제공된다.
 */

class SettingsActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BussroExceptionHandler.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initVar()
    }

    @SuppressLint("QueryPermissionsNeeded", "SetTextI18n")
    private fun initVar() {
        /* 뒤로가기 */
        binding.ivSettingBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        /* 공지사항 */
        binding.txtSettingNotiDesc.setOnClickListener {
            binding.ivSettingNotiDetail.callOnClick()
        }

        binding.ivSettingNotiDetail.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
                .putExtra("addr", NOTIFICATION_PAGE)
            startActivity(intent)
        }

        /* 문의하기 (이메일로 연결) */
        binding.txtSettingInquiryDesc.setOnClickListener {
            binding.ivSettingInquiryDetail.callOnClick()
        }

        binding.ivSettingInquiryDetail.setOnClickListener {
            SuggestionsDialog().show(supportFragmentManager, "SuggestionsDialog")
        }

        /* 개인정보처리방침 */
        binding.txtSettingClDesc.setOnClickListener {
            binding.ivSettingClDetail.callOnClick()
        }

        binding.ivSettingClDetail.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
                .putExtra("addr", CLAUSE_PAGE)
            startActivity(intent)
        }

        /* 오픈소스 및 라이센스 */
        binding.txtSettingOsDesc.setOnClickListener {
            binding.ivSettingOsDetail.callOnClick()
        }

        binding.ivSettingOsDetail.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
                .putExtra("addr", LICENSE_PAGE)
            startActivity(intent)
        }

        /* 음성안내 */
        binding.switchSetting.isChecked = SharedPrefManager.getTTS(this)

        binding.switchSetting.setOnCheckedChangeListener { buttonView, isChecked ->
            // 설정 저장
            SharedPrefManager.setTTS(this, isChecked)
            Log.d("TEST", "음성안내 사용자 설정 : $isChecked")
        }

        /* 버전정보 */
        binding.txtSettingVersion.text = "버전정보 ${BuildConfig.VERSION_NAME}"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }

    companion object {
        private const val NOTIFICATION_PAGE = "https://stitch-mandarin-baa.notion.site/70c0a410a7c6473b839962d06b107e59"
        private const val CLAUSE_PAGE= "https://stitch-mandarin-baa.notion.site/dafe04a6643d47f6a06127d87d323f28"
        private const val LICENSE_PAGE = "https://stitch-mandarin-baa.notion.site/daee86be24c14b3d8f50c66aafc753a1"
    }
}