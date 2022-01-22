package com.youreye.bussro.feature.error

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.youreye.bussro.R

class ErrorActivity : AppCompatActivity() {
    private val lastActivityIntent by lazy { intent.getParcelableExtra<Intent>(EXTRA_INTENT) }
    private val errorText by lazy { intent.getStringExtra(EXTRA_ERROR_TEXT) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        // 에러 이유
        findViewById<TextView>(R.id.txt_error_reason).text = errorText

        // 새로고침 버튼 click listener
        findViewById<AppCompatButton>(R.id.btn_error_refresh).setOnClickListener {
            startActivity(lastActivityIntent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }

    companion object {
        const val EXTRA_INTENT = "EXTRA_INTENT"
        const val EXTRA_ERROR_TEXT = "EXTRA_ERROR_TEXT"
    }
}