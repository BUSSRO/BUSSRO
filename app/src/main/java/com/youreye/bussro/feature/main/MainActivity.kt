package com.youreye.bussro.feature.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityMainBinding
import com.youreye.bussro.feature.dialog.BackPressDialog
import com.youreye.bussro.feature.info.InfoActivity
import com.youreye.bussro.util.BussroExceptionHandler
import java.lang.RuntimeException

/**
 * [MainActivity]
 * SplashActivity 후 보여짐
 * 사용자는 원하는 버튼을 선택해서 원하는 기능으로 이동할 수 있다.
 */

class MainActivity : AppCompatActivity() {
    private val model by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BussroExceptionHandler.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.model = model

        /* 앱 사용설명서 click listener */
        binding.txtMainInfo.setOnClickListener {
//            val intent = Intent(this, InfoActivity::class.java)
//            startActivity(intent)

            Toast.makeText(this, "다음 업데이트에 포함될 기능입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /* disable message "실행하려면 두 번 누르세요" */
    private fun removeAction() {
        val views = listOf(
            binding.imgMainSetting, binding.txtMainFirst, binding.txtMainSecond, binding.txtMainThird
        )

        for (view in views) {
            view.accessibilityDelegate = object: View.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View?,
                    info: AccessibilityNodeInfo?
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info?.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
                    info?.isClickable = false
                }
            }
        }
    }

    override fun onBackPressed() {
        val dialog = BackPressDialog()
        dialog.show(supportFragmentManager, "BackPressDialog")
    }
}