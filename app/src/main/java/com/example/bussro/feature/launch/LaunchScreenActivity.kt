package com.example.bussro.feature.launch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.bussro.feature.main.MainActivity
import com.example.bussro.feature.onboarding.OnBoardingActivity
import com.example.bussro.util.User
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * [LaunchScreenActivity]
 * 앱 구동시 보여줄 화면
 */

class LaunchScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OnBoardingActivity 와 MainActivity 로 갈 사용자 구분하기
        val intent = when (User.getFirst(this)) {
            true -> {
                Intent(this, OnBoardingActivity::class.java)
            }
            false -> {
                Intent(this, MainActivity::class.java)
            }
        }

        startActivity(intent)
        finish()
    }
}