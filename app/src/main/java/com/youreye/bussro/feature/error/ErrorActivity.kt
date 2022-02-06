package com.youreye.bussro.feature.error

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.youreye.bussro.R
import com.youreye.bussro.feature.dialog.SuggestionsDialog
import com.youreye.bussro.feature.main.MainActivity
import com.youreye.bussro.feature.onboarding.OnBoardingActivity
import com.youreye.bussro.util.SharedPrefManager

class ErrorActivity : AppCompatActivity() {
    private val lastActivityIntent by lazy { intent.getParcelableExtra<Intent>(EXTRA_INTENT) }
    private val errorText by lazy { intent.getStringExtra(EXTRA_ERROR_TEXT) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        // 에러 이유
        Log.d("ErrorActivity", "onCreate: $errorText")

        // 새로고침 버튼 click listener
        findViewById<TextView>(R.id.txt_error_refresh).setOnClickListener {

            if (SharedPrefManager.isFirst(this)) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }

            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        // 문의하기 click listener
        findViewById<TextView>(R.id.txt_error_suggestions).setOnClickListener {
            SuggestionsDialog().show(supportFragmentManager, "SuggestionsDialog")
        }
    }

    companion object {
        const val EXTRA_INTENT = "EXTRA_INTENT"
        const val EXTRA_ERROR_TEXT = "EXTRA_ERROR_TEXT"
    }
}