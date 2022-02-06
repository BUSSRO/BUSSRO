package com.youreye.bussro.feature.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.youreye.bussro.R

class DetailInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_info)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}