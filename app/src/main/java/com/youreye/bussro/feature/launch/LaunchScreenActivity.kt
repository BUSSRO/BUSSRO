package com.youreye.bussro.feature.launch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.youreye.bussro.R
import com.youreye.bussro.feature.main.MainActivity
import com.youreye.bussro.feature.onboarding.OnBoardingActivity
import com.youreye.bussro.util.User

/**
 * [LaunchScreenActivity]
 * 앱 실행시 보여줄 로딩 화면
 */

class LaunchScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_screen)

        Handler().postDelayed({
            // OnBoardingActivity 와 MainActivity 로 갈 사용자 구분하기
            val intent = when (User.isFirst(this)) {
                true -> {
                    Intent(this, OnBoardingActivity::class.java)
                }
                false -> {
                    Intent(this, MainActivity::class.java)
                }
            }

            startActivity(intent)
            finish()
        },1500)
    }
}