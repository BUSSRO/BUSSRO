package com.example.bussro.feature.launch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bussro.feature.main.MainActivity

/**
 * [LaunchScreenActivity]
 * 앱 구동시 보여줄 화면
 */

class LaunchScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}